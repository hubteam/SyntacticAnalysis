package com.wxw.modelUnused;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.wxw.sequence.SyntacticAnalysisBeamSearch;
import com.wxw.sequence.SyntacticAnalysisSequenceClassificationModel;
import com.wxw.tree.TreeNode;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.model.BaseModel;

/**
 * 一步训练得到的树模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisModel extends BaseModel{

	private static final String COMPONENT_NAME = "SyntacticAnalysisME";
	private static final String TREE_MODEL_ENTRY_NAME = "tree.model";
	
	/**
	 * 构造
	 * @param componentName 训练模型的类
	 * @param modelFile 模型文件
	 * @throws IOException IO异常
	 */
	protected SyntacticAnalysisModel(String componentName, File modelFile) throws IOException {
		super(COMPONENT_NAME, modelFile);
		
	}

	/**
	 * 构造
	 * @param languageCode 编码
	 * @param posModel 最大熵模型
	 * @param beamSize 大小
	 * @param manifestInfoEntries 配置的信息
	 */
	public SyntacticAnalysisModel(String languageCode, MaxentModel model, int beamSize,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (model == null) {
            throw new IllegalArgumentException("The maxentPosModel param must not be null!");
        }

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        //放入新训练出来的模型
        artifactMap.put(TREE_MODEL_ENTRY_NAME, model);
        checkArtifactMap();
	}
	

	public SyntacticAnalysisModel(String languageCode, SequenceClassificationModel<String> seqModel,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (seqModel == null) {
            throw new IllegalArgumentException("The maxent wordsegModel param must not be null!");
        }

        artifactMap.put(TREE_MODEL_ENTRY_NAME, seqModel);
		
	}

	/**
	 * 获取模型
	 * @return 最大熵模型
	 */
	public MaxentModel getTreeModel() {
		if (artifactMap.get(TREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(TREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SyntacticAnalysisSequenceClassificationModel<? extends TreeNode> getTreeSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(TREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = SyntacticAnalysisME.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new SyntacticAnalysisBeamSearch(beamSize, (MaxentModel) artifactMap.get(TREE_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(TREE_MODEL_ENTRY_NAME) instanceof SyntacticAnalysisSequenceClassificationModel) {
            return (SyntacticAnalysisSequenceClassificationModel) artifactMap.get(TREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }
}

