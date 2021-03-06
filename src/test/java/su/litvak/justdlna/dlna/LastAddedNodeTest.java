package su.litvak.justdlna.dlna;

import org.junit.Before;
import org.junit.Test;
import su.litvak.justdlna.Config;
import su.litvak.justdlna.model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LastAddedNodeTest extends AbstractTest {
    List<File> files = new ArrayList<File>();
    FolderNode<VideoFormat> folder;

    @Before
    public void prepareConfiguration() throws IOException, InterruptedException {
        folder = mockDir("Video", VideoFormat.class);

        for (int i = 0; i < 10; i++) {
            File f = mockFile("video " + i, VideoFormat.MKV, folder);
            files.add(f);
            f.setLastModified(System.currentTimeMillis() - i * 1000);
        }

        Config.get().setContent(new VirtualFolderNode(Arrays.<ContainerNode>asList(folder)));

    }

    @Test
    public void testVideoSorting() throws IOException {
        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(null, Formats.VIDEO.name(), 5);
        folder.addContainer(lastVideos);

        assertEquals(5, lastVideos.getItems().size());

        List<File> items = new ArrayList<File>();
        for (ItemNode item : lastVideos.getItems()) {
            items.add(item.getFile());
        }
        assertEquals(files.subList(0, 5), items);
    }

    @Test
    public void testUnderLimit() throws IOException {
        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(null, Formats.VIDEO.name(), files.size() + 1);
        folder.addContainer(lastVideos);
        assertEquals(files.size(), lastVideos.getItems().size());
    }

    @Test
    public void testNoContainers() throws IOException {
        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(null, Formats.VIDEO.name(), 5);
        folder.addContainer(lastVideos);
        assertEquals(0, lastVideos.getContainers().size());
    }

    @Test
    public void testViewLog() {
        ViewLog.init();
        ViewLog.clear();

        LastAddedNode<VideoFormat> lastVideos = new LastAddedNode<VideoFormat>(null, Formats.VIDEO.name(), 5);
        folder.addContainer(lastVideos);
        for (ItemNode item : lastVideos.getItems()) {
            for (int i = 0; i < 3; i++) {
                ViewLog.log(item.getFile(), item.getParent().getFormatClass());
            }
        }

        for (int i = 1; i < 10; i++) {
            LastViewedNode<VideoFormat> lastViewed = new LastViewedNode<VideoFormat>(null, Formats.VIDEO.name(), i);
            folder.addContainer(lastViewed);
            int count = Math.min(5, i);
            assertEquals(count, lastViewed.getItems().size());
            assertEquals(0, lastViewed.getContainers().size());
        }
    }
}
