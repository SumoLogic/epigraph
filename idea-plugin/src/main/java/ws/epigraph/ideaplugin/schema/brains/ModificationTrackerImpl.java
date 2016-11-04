package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.util.ModificationTracker;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ModificationTrackerImpl implements ModificationTracker {
  private int modificationCount;

  public void tick() {
    modificationCount++;
  }

  @Override
  public long getModificationCount() {
    return modificationCount;
  }
}
