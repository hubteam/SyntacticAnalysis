package com.wxw.model.bystep;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.wxw.model.all.unused.SyntacticAnalysisME;
import com.wxw.sequence.SyntacticAnalysisBeamSearch;
import com.wxw.sequence.SyntacticAnalysisSequenceClassificationModel;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.model.BaseModel;

/**
 * 分步骤训练得到的chunk模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisModelForChunk extends BaseModel{

	private static final String COMPONENT_NAME = "SyntacticAnalysisMEForChunk";
	private static final String CHUNKTREE_MODEL_ENTRY_NAME = "chunkTree.model";
	
	/**
	 * 构造
	 * @param componentName 训练模型的类
	 * @param modelFile 模型文件
	 * @throws IOException IO异常
	 */
	protected SyntacticAnalysisModelForChunk(String componentName, File modelFile) throws IOException {
		super(COMPONENT_NAME, modelFile);
		
	}

	/**
	 * 构造
	 * @param languageCode 编码
	 * @param model 最大熵模型
	 * @param beamSize 大小
	 * @param manifestInfoEntries 配置的信息
	 */
	public SyntacticAnalysisModelForChunk(String languageCode, MaxentModel model, int beamSize,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (model == null) {
            throw new IllegalArgumentException("The maxentPosModel param must not be null!");
        }

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);
        manifest.setProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER, Integer.toString(beamSize));

        //放入新训练出来的模型
        artifactMap.put(CHUNKTREE_MODEL_ENTRY_NAME, model);
        checkArtifactMap();
	}
	

	public SyntacticAnalysisModelForChunk(String languageCode, SequenceClassificationModel<String> seqModel,
			Map<String, String> manifestInfoEntries) {
		super(COMPONENT_NAME, languageCode, manifestInfoEntries, null);
		if (seqModel == null) {
            throw new IllegalArgumentException("The maxent chunkModel param must not be null!");
        }

        artifactMap.put(CHUNKTREE_MODEL_ENTRY_NAME, seqModel);
		
	}

	/**
	 * 获取模型
	 * @return 最大熵模型
	 */
	public MaxentModel getChunkTreeModel() {
		if (artifactMap.get(CHUNKTREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            return (MaxentModel) artifactMap.get(CHUNKTREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
	}
	
	public SyntacticAnalysisSequenceClassificationModel getChunkTreeSequenceModel() {

        Properties manifest = (Properties) artifactMap.get(MANIFEST_ENTRY);

        if (artifactMap.get(CHUNKTREE_MODEL_ENTRY_NAME) instanceof MaxentModel) {
            String beamSizeString = manifest.getProperty(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

            int beamSize = SyntacticAnalysisME.DEFAULT_BEAM_SIZE;
            if (beamSizeString != null) {
                beamSize = Integer.parseInt(beamSizeString);
            }

            return new SyntacticAnalysisBeamSearch(beamSize, (MaxentModel) artifactMap.get(CHUNKTREE_MODEL_ENTRY_NAME));
        } else if (artifactMap.get(CHUNKTREE_MODEL_ENTRY_NAME) instanceof SyntacticAnalysisSequenceClassificationModel) {
            return (SyntacticAnalysisSequenceClassificationModel) artifactMap.get(CHUNKTREE_MODEL_ENTRY_NAME);
        } else {
            return null;
        }
    }
}

