package com.wxw.modelBystep;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wxw.feature.SyntacticAnalysisContextGenerator;
import com.wxw.headwords.AbsractGenerateHeadWords;
import com.wxw.headwords.ConcreteGenerateHeadWords;
import com.wxw.headwords.HeadWordsRuleSet;
import com.wxw.sequence.DefaultSyntacticAnalysisSequenceValidator;
import com.wxw.sequence.SyntacticAnalysisBeamSearch;
import com.wxw.sequence.SyntacticAnalysisSequenceClassificationModel;
import com.wxw.sequence.SyntacticAnalysisSequenceForBuildAndCheck;
import com.wxw.sequence.SyntacticAnalysisSequenceValidator;
import com.wxw.stream.FileInputStreamFactory;
import com.wxw.stream.PlainTextByTreeStream;
import com.wxw.stream.SyntacticAnalysisSample;
import com.wxw.stream.SyntacticAnalysisSampleStream;
import com.wxw.syntacticanalysis.ConstituentTree;
import com.wxw.syntacticanalysis.SyntacticAnalysis;
import com.wxw.tree.HeadTreeNode;
import com.wxw.tree.HeadTreeToActions;
import com.wxw.tree.PhraseGenerateTree;
import com.wxw.tree.TreeNode;
import com.wxw.tree.TreeToHeadTree;

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;
/**
 * 分步骤训练build check模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForBuildAndCheck implements SyntacticAnalysis<HeadTreeNode>{
	public static final int DEFAULT_BEAM_SIZE = 20;
	private SyntacticAnalysisContextGenerator<HeadTreeNode> contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SyntacticAnalysisSequenceClassificationModel<HeadTreeNode> model;
	private SyntacticAnalysisModelForBuildAndCheck modelPackage;

    private SyntacticAnalysisSequenceValidator<HeadTreeNode> sequenceValidator;
    
    private AbsractGenerateHeadWords aghw = new ConcreteGenerateHeadWords(); 
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public SyntacticAnalysisMEForBuildAndCheck(SyntacticAnalysisModelForBuildAndCheck model, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(SyntacticAnalysisModelForBuildAndCheck model, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) {
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidator();
     
        this.model = new SyntacticAnalysisBeamSearch(beamSize,model.getBuildTreeModel(),
                    model.getCheckTreeModel(), 0);
	}
	
	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SyntacticAnalysisModelForBuildAndCheck train(File file, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			String encoding){
		SyntacticAnalysisModelForBuildAndCheck model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForBuildAndCheck.train("zh", sampleStream, params, contextGen);
			return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}

	/**
	 * 训练模型
	 * @param languageCode 编码
	 * @param sampleStream 文件流
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static SyntacticAnalysisModelForBuildAndCheck train(String languageCode, ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel buildModel = null;
        MaxentModel checkModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> buildseqModel = null;
        SequenceClassificationModel<String> checkseqModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> buildes = new SyntacticAnalysisSampleEventForBuild(sampleStream, contextGen);
            EventTrainer buildtrainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            buildModel = buildtrainer.train(buildes);   
            sampleStream.reset();
            ObjectStream<Event> checkes = new SyntacticAnalysisSampleEventForCheck(sampleStream, contextGen);
            EventTrainer checktrainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            checkModel = checktrainer.train(checkes); 
        }

        if (buildModel != null && checkModel != null) {
            return new SyntacticAnalysisModelForBuildAndCheck(languageCode, buildModel, checkModel, beamSize, manifestInfoEntries);
        } else {
            return new SyntacticAnalysisModelForBuildAndCheck(languageCode, buildseqModel, checkseqModel, manifestInfoEntries);
        }
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 训练的文本
	 * @param buildmodeltxtFile 文本类型的模型文件
	 * @param checkmodeltxtFile 文本类型的模型文件
	 * @param params 训练的参数配置
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelForBuildAndCheck train(File file, File buildAndCheckmodelFile, 
			TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, String encoding) {
//		PlainTextGISModelWriter modelWriter = null;
		OutputStream modelOut = null;
		SyntacticAnalysisModelForBuildAndCheck model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream);
			model = SyntacticAnalysisMEForBuildAndCheck.train("zh", sampleStream, params, contextGen);
			modelOut = new BufferedOutputStream(new FileOutputStream(buildAndCheckmodelFile));           
            model.serialize(modelOut);
//            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getBuildTreeModel(), buildmodeltxtFile);
//            modelWriter.persist();
//            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getCheckTreeModel(), checkmodeltxtFile);
//            modelWriter.persist();
            return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			
            if (modelOut != null) {
                try {
                	modelOut.close();
//                	modelWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		return null;
	}
	
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static SyntacticAnalysisModelForBuildAndCheck readModel(File buildmodelFile, File checkmodelFile, TrainingParameters params, SyntacticAnalysisContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel buildModel = null;
		AbstractModel checkModel = null;
		SyntacticAnalysisModelForBuildAndCheck model = null;
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(buildmodelFile);			
			buildModel = modelReader.getModel();
			modelReader = new PlainTextGISModelReader(checkmodelFile);			
			checkModel = modelReader.getModel();
			model =  new SyntacticAnalysisModelForBuildAndCheck(encoding, buildModel, checkModel, beamSize,manifestInfoEntries);
	
			System.out.println("读取模型成功");
            return model;
        } catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 得到最好的K个最好结果的树,List中每一个值都是一颗完整的树
	 * @param k 结果数目
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public List<HeadTreeNode> tagBuildAndCheck(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac){
		List<HeadTreeNode> buildAndCheckTree = new ArrayList<>();
		SyntacticAnalysisSequenceForBuildAndCheck<HeadTreeNode>[] sequences = this.model.bestSequencesForBuildAndCheck(k, chunkTree, ac, contextGenerator, sequenceValidator);
		if(sequences == null){
			return null;
		}else{
			for (int i = 0; i < sequences.length; i++) {
				buildAndCheckTree.add(sequences[i].getTree().get(0));
			}
			return buildAndCheckTree;
		}
	}
	
	/**
	 * 得到最好的K个完整的动作序列
	 * @param k 结果数
	 * @param chunkTree k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<List<String>> tagKactions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac) throws CloneNotSupportedException{
		List<List<String>> kActions = new ArrayList<>();
		List<HeadTreeNode> alltree= tagBuildAndCheck(k,chunkTree,null);
		if(alltree == null){
			return null;
		}else{
			for (int i = 0; i < alltree.size(); i++) {
				HeadTreeToActions tta = new HeadTreeToActions();
				PhraseGenerateTree pgt = new PhraseGenerateTree();
				TreeToHeadTree ttht = new TreeToHeadTree();
				TreeNode node = pgt.generateTree("("+alltree.get(i).toBracket()+")");
				HeadTreeNode headTree = ttht.treeToHeadTree(node);
				SyntacticAnalysisSample<HeadTreeNode> sample = tta.treeToAction(headTree);
				kActions.add(sample.getActions());	
			}
			return kActions;
		}
	}
	
	/**
	 * 得到最好的K个完整的动作序列
	 * @param k 结果数
	 * @param chunkTree k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<String> tagActions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac) throws CloneNotSupportedException{
		List<List<String>> kActions = tagKactions(1,chunkTree,null);
		
		return kActions.get(0);
	}
	
	/**
	 * 得到最好的树
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public HeadTreeNode tagBuildAndCheck(List<List<HeadTreeNode>> chunkTree, Object[] ac){
		List<HeadTreeNode> buildAndCheckTree = tagBuildAndCheck(1,chunkTree, ac);
		if(buildAndCheckTree == null){
			return null;
		}else{
			return buildAndCheckTree.get(0);
		}
	}
	/**
	 * 得到句法树
	 * @param chunkTree chunk子树序列
	 * @return
	 */
	@Override
	public ConstituentTree syntacticTree(List<HeadTreeNode> chunkTree) {
		List<List<HeadTreeNode>> allTree = new ArrayList<>();
		allTree.add(chunkTree);
		HeadTreeNode headTreeNode = tagBuildAndCheck(allTree,null);
		ConstituentTree constituent = new ConstituentTree();
		constituent.setTreeNode(headTreeNode);
		return constituent;
	}
	/**
	 * 得到句法树
	 * @param words 词语
	 * @param poses 词性标记
	 * @param chunkTag chunk标记
	 * @return
	 */
	@Override
	public ConstituentTree syntacticTree(String[] words, String[] poses, String[] chunkTag) {
		List<HeadTreeNode> chunkTree = toChunkTreeList(words,poses,chunkTag);
		return syntacticTree(chunkTree);
	}
	
	public List<HeadTreeNode> toChunkTreeList(String[] words, String[] poses, String[] chunkTag){
		List<HeadTreeNode> chunkTree = new ArrayList<>();
		for (int i = 0; i < chunkTag.length; i++) {
			if(chunkTag[i].equals("O")){
				HeadTreeNode pos = new HeadTreeNode(poses[i]);
				pos.addChild(new HeadTreeNode(words[i]));
				pos.setHeadWords(words[i]);
				chunkTree.add(pos);
			}else if(chunkTag[i].endsWith("B")){
				HeadTreeNode node = new HeadTreeNode(chunkTag[i].split("_")[0]);
				int j ;
				for (j = i; j < chunkTag.length; j++) {
					HeadTreeNode pos = new HeadTreeNode(poses[j]);
					pos.addChild(new HeadTreeNode(words[j]));
					pos.setHeadWords(words[j]);
					node.addChild(pos);
					if(chunkTag[j].endsWith("E")){
						break;
					}
				}
				node.setHeadWords(aghw.extractHeadWords(node, HeadWordsRuleSet.getNormalRuleSet(), HeadWordsRuleSet.getSpecialRuleSet()));
				chunkTree.add(node);
				i = j;
			}
		}
		return chunkTree;
	}
	
	/**
	 * 得到句法树
	 * @param sentence 由词语词性标记和chunk标记组成的句子,输入的格式[wods/pos word/pos...]tag
	 * @return
	 */
	@Override
	public ConstituentTree syntacticTree(String sentence) {	
		return syntacticTree(1,sentence)[0];
	}
	/**
	 * 得到句法树的括号表达式
	 * @param chunkTree chunk子树序列
	 * @return
	 */
	@Override
	public String syntacticBracket(List<HeadTreeNode> chunkTree) {
		HeadTreeNode node = (HeadTreeNode) syntacticTree(chunkTree).getTreeNode();
		return HeadTreeNode.printTree(node, 1);
	}
	/**
	 * 得到句法树的括号表达式
	 * @param words 词语
	 * @param poses 词性标记
	 * @param chunkTag chunk标记
	 * @return
	 */
	@Override
	public String syntacticBracket(String[] words,String[] poses, String[] chunkTag) {
		HeadTreeNode node = (HeadTreeNode) syntacticTree(words,poses,chunkTag).getTreeNode();
		return HeadTreeNode.printTree(node, 1);
	}
	/**
	 * 得到句法树的括号表达式
	 * @param sentence 由词语词性标记和chunk标记组成的句子
	 * @return
	 */
	@Override
	public String syntacticBracket(String sentence) {
		HeadTreeNode node = (HeadTreeNode) syntacticTree(sentence).getTreeNode();
		return HeadTreeNode.printTree(node, 1);
	}
	@SuppressWarnings("unchecked")
	@Override
	public ConstituentTree[] syntacticTree(int k, List<HeadTreeNode> chunkTree) {
		List<List<HeadTreeNode>> allTree = new ArrayList<>();
		allTree.add(chunkTree);
		List<HeadTreeNode> headTreeNode = tagBuildAndCheck(k,allTree,null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++) {
			ConstituentTree con = new ConstituentTree();
			con.setTreeNode(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}
	@Override
	public ConstituentTree[] syntacticTree(int k, String[] words, String[] poses, String[] chunkTag) {
		List<HeadTreeNode> chunkTree = toChunkTreeList(words,poses,chunkTag);
		return syntacticTree(k,chunkTree);
	}
	@Override
	public ConstituentTree[] syntacticTree(int k, String sentence) {
		List<String> chunkTags = new ArrayList<>();
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();
		
		boolean isInChunk = false;							//当前词是否在组块中
		List<String> wordTagsInChunk = new ArrayList<>();	//临时存储在组块中的词与词性
		String[] wordTag = null;							//词与词性标注
		String chunk = null;								//组块的标签
		String[] content = sentence.split("\\s+");
		for(String string : content) {
			if(isInChunk) {	//当前词在组块中
				if(string.contains("]")) {//当前词是组块的结束
					String[] strings = string.split("]");
					wordTagsInChunk.add(strings[0]);
					chunk = strings[1];
					isInChunk = false;
				}else {
					wordTagsInChunk.add(string);
				}
			}else {//当前词不在组块中
				if(wordTagsInChunk != null && chunk != null) {//上一个组块中的词未处理，先处理上一个组块中的词	
					wordTag = wordTagsInChunk.get(0).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + "_B");
					
					if(wordTagsInChunk.size() > 2) {
						for(int i = 1; i < wordTagsInChunk.size() - 1; i++) {
							wordTag = wordTagsInChunk.get(i).split("/");
							words.add(wordTag[0]);
							poses.add(wordTag[1]);
							chunkTags.add(chunk + "_I");
						}
					}
					wordTag = wordTagsInChunk.get(wordTagsInChunk.size() - 1).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + "_E");
					
					wordTagsInChunk = new ArrayList<>();
					chunk = null;
					
					if(string.startsWith("[")) {
						wordTagsInChunk.add(string.replace("[", ""));
						isInChunk = true;
					}else {
						wordTag = string.split("/");
						words.add(wordTag[0]);
						poses.add(wordTag[1]);
						chunkTags.add("_O");
					}
					
				}else {
					if(string.startsWith("[")) {
						wordTagsInChunk.add(string.replace("[", ""));
						isInChunk = true;
					}else {
						wordTag = string.split("/");
						words.add(wordTag[0]);
						poses.add(wordTag[1]);
						chunkTags.add("_O");
					}
				}
			}
		}
		
		//句子结尾是组块，进行解析
		if(wordTagsInChunk != null && chunk != null) {
			wordTag = wordTagsInChunk.get(0).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			chunkTags.add(chunk + "_B");
			
			if(wordTagsInChunk.size() > 2) {
				for(int i = 1; i < wordTagsInChunk.size() - 1; i++) {
					wordTag = wordTagsInChunk.get(i).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + "_I");
				}
			}
			wordTag = wordTagsInChunk.get(wordTagsInChunk.size() - 1).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			chunkTags.add(chunk + "_E");
		}
		return syntacticTree(k,words.toArray(new String[words.size()]),poses.toArray(new String[poses.size()]),
				chunkTags.toArray(new String[chunkTags.size()]));
	}
}

