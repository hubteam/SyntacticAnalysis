package com.wxw.model.bystep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.syntacticanalysis.SyntacticAnalysisForPos;
import com.wxw.tree.HeadTreeNode;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventModelSequenceTrainer;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.postag.MutableTagDictionary;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSSampleEventStream;
import opennlp.tools.postag.POSSampleSequenceStream;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.StringList;
import opennlp.tools.util.StringUtil;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.StringPattern;

/**
 * 词性标注模型训练类【为了改动最大熵词性标注的beamsize增加的类】
 * @author 王馨苇
 *
 */
public class POSTaggerMEExtend  implements POSTagger,SyntacticAnalysisForPos<HeadTreeNode> {
	public static final int DEFAULT_BEAM_SIZE = 20;
	private POSModel modelPackage;
	protected POSContextGenerator contextGen;
	protected TagDictionary tagDictionary;
	protected Dictionary ngramDictionary;
	protected boolean useClosedClassTagsFilter = false;
	protected int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
	private SequenceValidator<String> sequenceValidator;

	public POSTaggerMEExtend(POSModel model) {
		POSTaggerFactory factory = model.getFactory();
		int beamSize = POSTaggerMEExtend.DEFAULT_BEAM_SIZE;

		this.modelPackage = model;
		this.contextGen = factory.getPOSContextGenerator(beamSize);
		this.tagDictionary = factory.getTagDictionary();
		this.size = beamSize;
		this.sequenceValidator = factory.getSequenceValidator();
//		if (model.getPosSequenceModel() != null) {
//			this.model = model.getPosSequenceModel();
//		} else {
			this.model = new BeamSearch(beamSize, model.getPosModel(), 0);
//		}

	}

	public String[] getAllPosTags() {
		return this.model.getOutcomes();
	}

	public String[] tag(String[] sentence) {
		return this.tag(sentence, (Object[]) null);
	}

	public String[] tag(String[] sentence, Object[] additionaContext) {
		this.bestSequence = this.model.bestSequence(sentence, additionaContext, this.contextGen,
				this.sequenceValidator);
		List t = this.bestSequence.getOutcomes();
		return (String[]) t.toArray(new String[t.size()]);
	}

	public String[][] tag(int numTaggings, String[] sentence) {
		Sequence[] bestSequences = this.model.bestSequences(numTaggings, sentence, (Object[]) null, this.contextGen,
				this.sequenceValidator);
		String[][] tags = new String[bestSequences.length][];

		for (int si = 0; si < tags.length; ++si) {
			List t = bestSequences[si].getOutcomes();
			tags[si] = (String[]) t.toArray(new String[t.size()]);
		}

		return tags;
	}

	public Sequence[] topKSequences(String[] sentence) {
		return this.topKSequences(sentence, (Object[]) null);
	}

	public Sequence[] topKSequences(String[] sentence, Object[] additionaContext) {
		return this.model.bestSequences(this.size, sentence, additionaContext, this.contextGen, this.sequenceValidator);
	}

	public void probs(double[] probs) {
		this.bestSequence.getProbs(probs);
	}

	public double[] probs() {
		return this.bestSequence.getProbs();
	}

	public String[] getOrderedTags(List<String> words, List<String> tags, int index) {
		return this.getOrderedTags(words, tags, index, (double[]) null);
	}

	public String[] getOrderedTags(List<String> words, List<String> tags, int index, double[] tprobs) {
		if (this.modelPackage.getPosModel() == null) {
			throw new UnsupportedOperationException(
					"This method can only be called if the classifcation model is an event model!");
		} else {
			MaxentModel posModel = this.modelPackage.getPosModel();
			double[] probs = posModel
					.eval(this.contextGen.getContext(index, (String[]) words.toArray(new String[words.size()]),
							(String[]) tags.toArray(new String[tags.size()]), (Object[]) null));
			String[] orderedTags = new String[probs.length];

			for (int i = 0; i < probs.length; ++i) {
				int max = 0;

				for (int ti = 1; ti < probs.length; ++ti) {
					if (probs[ti] > probs[max]) {
						max = ti;
					}
				}

				orderedTags[i] = posModel.getOutcome(max);
				if (tprobs != null) {
					tprobs[i] = probs[max];
				}

				probs[max] = 0.0D;
			}

			return orderedTags;
		}
	}

	public static POSModel train(String languageCode, ObjectStream<POSSample> samples, TrainingParameters trainParams,
			POSTaggerFactory posFactory) throws IOException {
		String beamSizeString = (String) trainParams.getSettings().get("BeamSize");
		int beamSize = 3;
		if (beamSizeString != null) {
			beamSize = Integer.parseInt(beamSizeString);
		}

		POSContextGenerator contextGenerator = posFactory.getPOSContextGenerator();
		HashMap manifestInfoEntries = new HashMap();
		TrainerType trainerType = TrainerFactory.getTrainerType(trainParams.getSettings());
		MaxentModel posModel = null;
		SequenceClassificationModel seqPosModel = null;
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
			POSSampleEventStream trainer = new POSSampleEventStream(samples, contextGenerator);
			EventTrainer ss = TrainerFactory.getEventTrainer(trainParams.getSettings(), manifestInfoEntries);
			posModel = ss.train(trainer);
		} else if (TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType)) {
			POSSampleSequenceStream trainer1 = new POSSampleSequenceStream(samples, contextGenerator);
			EventModelSequenceTrainer ss1 = TrainerFactory.getEventModelSequenceTrainer(trainParams.getSettings(),
					manifestInfoEntries);
			posModel = ss1.train(trainer1);
		} else {
			if (!TrainerType.SEQUENCE_TRAINER.equals(trainerType)) {
				throw new IllegalArgumentException("Trainer type is not supported: " + trainerType);
			}

			SequenceTrainer trainer2 = TrainerFactory.getSequenceModelTrainer(trainParams.getSettings(),
					manifestInfoEntries);
			POSSampleSequenceStream ss2 = new POSSampleSequenceStream(samples, contextGenerator);
			seqPosModel = trainer2.train(ss2);
		}

		return posModel != null ? new POSModel(languageCode, posModel, beamSize, manifestInfoEntries, posFactory)
				: new POSModel(languageCode, seqPosModel, manifestInfoEntries, posFactory);
	}

	public static Dictionary buildNGramDictionary(ObjectStream<POSSample> samples, int cutoff) throws IOException {
		NGramModel ngramModel = new NGramModel();

		POSSample sample;
		while ((sample = (POSSample) samples.read()) != null) {
			String[] words = sample.getSentence();
			if (words.length > 0) {
				ngramModel.add(new StringList(words), 1, 1);
			}
		}

		ngramModel.cutoff(cutoff, Integer.MAX_VALUE);
		return ngramModel.toDictionary(true);
	}

	public static void populatePOSDictionary(ObjectStream<POSSample> samples, MutableTagDictionary dict, int cutoff)
			throws IOException {
		System.out.println("Expanding POS Dictionary ...");
		long start = System.nanoTime();
		HashMap newEntries = new HashMap();

		POSSample sample;
		while ((sample = (POSSample) samples.read()) != null) {
			String[] words = sample.getSentence();
			String[] wordEntry = sample.getTags();

			for (int tagsForWord = 0; tagsForWord < words.length; ++tagsForWord) {
				if (!StringPattern.recognize(words[tagsForWord]).containsDigit()) {
					String word;
					if (dict.isCaseSensitive()) {
						word = words[tagsForWord];
					} else {
						word = StringUtil.toLowerCase(words[tagsForWord]);
					}

					if (!newEntries.containsKey(word)) {
						newEntries.put(word, new HashMap());
					}

					String[] entry = dict.getTags(word);
					if (entry != null) {
						String[] arg11 = entry;
						int arg12 = entry.length;

						for (int arg13 = 0; arg13 < arg12; ++arg13) {
							String tag = arg11[arg13];
							Map value = (Map) newEntries.get(word);
							if (!value.containsKey(tag)) {
								value.put(tag, new AtomicInteger(cutoff));
							}
						}
					}

					if (!((Map) newEntries.get(word)).containsKey(wordEntry[tagsForWord])) {
						((Map) newEntries.get(word)).put(wordEntry[tagsForWord], new AtomicInteger(1));
					} else {
						((AtomicInteger) ((Map) newEntries.get(word)).get(wordEntry[tagsForWord])).incrementAndGet();
					}
				}
			}
		}

		Iterator arg16 = newEntries.entrySet().iterator();

		while (arg16.hasNext()) {
			Entry arg17 = (Entry) arg16.next();
			ArrayList arg18 = new ArrayList();
			Iterator arg19 = ((Map) arg17.getValue()).entrySet().iterator();

			while (arg19.hasNext()) {
				Entry arg20 = (Entry) arg19.next();
				if (((AtomicInteger) arg20.getValue()).get() >= cutoff) {
					arg18.add(arg20.getKey());
				}
			}

			if (arg18.size() > 0) {
				dict.put((String) arg17.getKey(), (String[]) arg18.toArray(new String[arg18.size()]));
			}
		}

		System.out.println("... finished expanding POS Dictionary. [" + (System.nanoTime() - start) / 1000000L + "ms]");
	}

	/**
	 * 得到最好的词性标注子树序列
	 * @param words 词语序列
	 * @return
	 */
	public List<HeadTreeNode> tagPos(String[] words){
		return tagKpos(1,words).get(0);
	}
	/**
	 * 得到最好的K个词性标注子树序列
	 * @param k 个数
	 * @param words 词语
	 * @return
	 */
	public List<List<HeadTreeNode>> tagKpos(int k, String[] words){
		String[][] poses = tag(k, words);
		return toPosTree(words, poses);
	}
	
	/**
	 * 将词性标注和词语转成树的形式
	 * @param words k个最好的词语序列
	 * @param poses k个最好的词性标注序列
	 * @return
	 */
	public static List<List<HeadTreeNode>> toPosTree(String[] words, String[][] poses){
		List<List<HeadTreeNode>> posTrees = new ArrayList<>();
		for (int i = 0; i < poses.length; i++) {
			List<HeadTreeNode> posTree = new ArrayList<HeadTreeNode>();
			for (int j = 0; j < poses[i].length && j < words.length; j++) {
				HeadTreeNode pos = new HeadTreeNode(poses[i][j]);
				HeadTreeNode word = new HeadTreeNode(words[j]);
				pos.addChild(word);
				word.setParent(pos);
				pos.setHeadWords(words[j]);
				posTree.add(pos);
			}
			posTrees.add(posTree);
		}
		return posTrees;
	}
	
	/**
	 * 得到词性标注的结果
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public String[] pos(String[] words) {
		String[] poses = tag(words);
		String[] output = new String[poses.length];
		for (int i = 0; i < poses.length && i < words.length; i++) {
			output[i] = words[i]+"/"+poses[i];
		}
		return output;
	}

	/**
	 * 得到词性标注结果
	 * @param sentence 分词的句子
	 * @return
	 */
	@Override
	public String[] pos(String sentence) {
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentence);
		return pos(words);
	}

	/**
	 * 得到词性标注子树序列
	 * @param words 分词数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String[] words) {
		
		return tagPos(words);
	}

	/**
	 * 得到词性标注子树序列
	 * @param sentece 分词句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String sentece) {
		String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentece);
		return posTree(words);
	}
}
