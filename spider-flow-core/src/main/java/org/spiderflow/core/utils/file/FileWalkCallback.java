package org.spiderflow.core.utils.file;

import java.nio.file.Path;

/**
 * @author yida
 * @date 2024-08-14 19:01
 * @description Type your description over here.
 */
public interface FileWalkCallback {

	void fileWalkFinished(Path sourcePath);
}
