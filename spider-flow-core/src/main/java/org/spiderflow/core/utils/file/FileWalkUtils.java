package org.spiderflow.core.utils.file;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author yida
 * @date 2024-08-14 18:57
 * @description Type your description over here.
 */
public class FileWalkUtils {
	public static Path walkFileTree(Path start, FileVisitor<? super Path> visitor)
			throws IOException {
		return walkFileTree(start, visitor, (FileWalkCallback) null);
	}

	public static Path walkFileTree(Path start, FileVisitor<? super Path> visitor, FileWalkCallback fileWalkCallback)
			throws IOException {
		return walkFileTree(start,
				EnumSet.noneOf(FileVisitOption.class),
				Integer.MAX_VALUE,
				visitor, fileWalkCallback);
	}

	public static Path walkFileTree(Path start,
									Set<FileVisitOption> options,
									int maxDepth,
									FileVisitor<? super Path> visitor)
			throws IOException {
		return walkFileTree(start, options, maxDepth, visitor, (FileWalkCallback) null);
	}

	public static Path walkFileTree(Path start,
									Set<FileVisitOption> options,
									int maxDepth,
									FileVisitor<? super Path> visitor, FileWalkCallback fileWalkCallback)
			throws IOException {
		/**
		 * Create a FileTreeWalker to walk the file tree, invoking the visitor
		 * for each event.
		 */
		try (FileTreeWalker walker = new FileTreeWalker(options, maxDepth)) {
			FileTreeWalker.Event ev = walker.walk(start);
			do {
				FileVisitResult result;
				switch (ev.type()) {
					case ENTRY:
						IOException ioe = ev.ioeException();
						if (ioe == null) {
							assert ev.attributes() != null;
							result = visitor.visitFile(ev.file(), ev.attributes());
						} else {
							result = visitor.visitFileFailed(ev.file(), ioe);
						}
						break;
					case START_DIRECTORY:
						result = visitor.preVisitDirectory(ev.file(), ev.attributes());

						// if SKIP_SIBLINGS and SKIP_SUBTREE is returned then
						// there shouldn't be any more events for the current
						// directory.
						if (result == FileVisitResult.SKIP_SUBTREE ||
								result == FileVisitResult.SKIP_SIBLINGS)
							walker.pop();
						break;

					case END_DIRECTORY:
						result = visitor.postVisitDirectory(ev.file(), ev.ioeException());
						// SKIP_SIBLINGS is a no-op for postVisitDirectory
						if (result == FileVisitResult.SKIP_SIBLINGS)
							result = FileVisitResult.CONTINUE;
						break;
					default:
						throw new AssertionError("Should not get here");
				}

				if (Objects.requireNonNull(result) != FileVisitResult.CONTINUE) {
					if (result == FileVisitResult.TERMINATE) {
						break;
					} else if (result == FileVisitResult.SKIP_SIBLINGS) {
						walker.skipRemainingSiblings();
					}
				}
				ev = walker.next();
			} while (ev != null);
		}
		if (null != fileWalkCallback) {
			fileWalkCallback.fileWalkFinished(start);
		}
		return start;
	}
}
