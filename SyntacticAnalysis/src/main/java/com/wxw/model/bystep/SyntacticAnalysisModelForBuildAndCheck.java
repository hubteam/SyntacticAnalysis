package com.wxw.model.bystep;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.wxw.sequence.SyntacticAnalysisBeamSearch;
import com.wxw.sequence.SyntacticAnalysisSequenceClassificationModel;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.model.BaseModel;
/**
 * 分步骤训练的build和check模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisModelForBuildAndCheck extends BaseModel{

	private static final String COMPONENT_NAME = "SyntacticAnalysisMEForBuildAndCheck";
	private static final String CHECKTREE_MODEL_ENTRY_NAME = "checkTree.model";
	private static final String BUILDTREE_MODEL_ENTRY_NAME = "buildTree.model";
	/**
	 * 构造
	 * @param componentName 训练模型的类
	 * @param modelFile 模型文件
	 * @throws IOException IO异常
	 */
	protected SyntacticAnalysisModelForBuildAndCheck(String componentName, File modelFile) throws IOException {
		super(COMPONENT_NAME, modelFile);
		
	}

	/**
	 * 构造
	 * @param languageCode 编码
	 * @param model 最大熵模型
	 * @param beamSize 大小
	 * @param manifestInfoEntries 配置的信息
	 */
	public SyntacticAnalysisModelForBuildAndCheck(String languageCode, MaxentModel buildmodel, MaxentModel checkmodel, int beamSize,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (buildmodel == null) {
            throw new IllegalArgumentException("The maxentPosModel param must not be null!");
        }
		if (checkmodel == null) {
            throw new IllegalArgumentException("The maxentPosModel param must not be null!");
        }

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        //放入新训练出来的模型
        artifactMap.put(BUILDTREE_MODEL_ENTRY_NAME, buildmodel);
        artifactMap.put(CHECKTREE_MODEL_ENTRY_NAME, checkmodel);
        checkArtifactMap();
	}
	

	public SyntacticAnalysisModelForBuildAndCheck(String languageCode, SequenceClassificationModel<String> buildseqModel,
			SequenceClassificationModel<String> checkseqModel,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (buildseqModel == null) {
            throw new IllegalArgumentException("The maxent wordsegModel param must not be null!");
        }
		if (checkseqModel == null) {
            throw new IllegalArgumentException("The maxent wordsegModel param must not be null!");
        }

        artifactMap.put(BUILDTREE_MODEL_ENTRY_NAME, buildseqModel);
        artifactMap.put(CHECKTREE_MODEL_ENTRY_NAME, checkseqModel);
	}

	/**
	 * 获取check模型
	 * @return 最大熵模型
	 */
	public MaxentModel getCheckTreeModel() {
		if (artifactMap.get(CHECKTREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(CHECKTREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
	
	/**
	 * 获取build模型
	 * @return 最大熵模型
	 */
	public MaxentModel getBuildTreeModel() {
		if (artifactMap.get(BUILDTREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(BUILDTREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
	
	public SyntacticAnalysisSequenceClassificationModel getCheckTreeSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(CHECKTREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new SyntacticAnalysisBeamSearch(beamSize, (MaxentModel) artifactMap.get(CHECKTREE_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(CHECKTREE_MODEL_ENTRY_NAME) instanceof SyntacticAnalysisSequenceClassificationModel) {
            return (SyntacticAnalysisSequenceClassificationModel) artifactMap.get(CHECKTREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }
	
	public SyntacticAnalysisSequenceClassificationModel getBuildTreeSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(BUILDTREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new SyntacticAnalysisBeamSearch(beamSize, (MaxentModel) artifactMap.get(BUILDTREE_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(BUILDTREE_MODEL_ENTRY_NAME) instanceof SyntacticAnalysisSequenceClassificationModel) {
            return (SyntacticAnalysisSequenceClassificationModel) artifactMap.get(BUILDTREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }
}