package net.katsuster.test;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import net.katsuster.scenario.TreeNode;

public class TreeNodeTest {
    @Test
    public void testAddPathSingle() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        String path = "aaa/bbbb/cccc";

        root.addPath(path.split("/"));

        Optional<TreeNode<String>> node = root.findChild("aaa");
        Assert.assertNotNull("Failed to add 1st node.", root.findChild("aaa").orElse(null));
        Assert.assertNull("Add wrong 1st node.", root.findChild("aaaa").orElse(null));
        Assert.assertNull("Add wrong 1st node.", root.findChild("bbbb").orElse(null));
        Assert.assertNull("Add wrong 1st node.", root.findChild("cccc").orElse(null));

        TreeNode<String> nodeA = root.findChild("aaa").get();
        Assert.assertNotNull("Failed to add 2nd node.", nodeA.findChild("bbbb").orElse(null));
        Assert.assertNull("Add wrong 2nd node.", nodeA.findChild("bbbbb").orElse(null));
        Assert.assertNull("Add wrong 2nd node.", nodeA.findChild("aaa").orElse(null));
        Assert.assertNull("Add wrong 2nd node.", nodeA.findChild("cccc").orElse(null));

        TreeNode<String> nodeB = nodeA.findChild("bbbb").get();
        Assert.assertNotNull("Failed to add 3rd node.", nodeB.findChild("cccc").orElse(null));
        Assert.assertNull("Add wrong 3rd node.", nodeB.findChild("ccccc").orElse(null));
        Assert.assertNull("Add wrong 3rd node.", nodeB.findChild("aaa").orElse(null));
        Assert.assertNull("Add wrong 3rd node.", nodeB.findChild("bbbb").orElse(null));
    }

    @Test
    public void testAddPathMulti() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        String[] paths = {
                "aaaa/bbbb/ccc",
                "aaa/bbbb/cccc",
                "aaa/bbbb/ccccc",
                "aaa/bbbbb/cccc",
                "aaa/bbbbb/ccccc",
                "aaaa/bbbb/cccc",
        };

        for (String path : paths) {
            root.addPath(path.split("/"));
        }

        Assert.assertNotNull("Failed to add 1st path.",
                root.findChild("aaa").get()
                        .findChild("bbbb").get()
                        .findChild("cccc").orElse(null));
        Assert.assertNotNull("Failed to add 2nd path.",
                root.findChild("aaa").get()
                        .findChild("bbbb").get()
                        .findChild("ccccc").orElse(null));
        Assert.assertNotNull("Failed to add 3rd path.",
                root.findChild("aaa").get()
                        .findChild("bbbbb").get()
                        .findChild("cccc").orElse(null));
        Assert.assertNotNull("Failed to add 4th path.",
                root.findChild("aaa").get()
                        .findChild("bbbbb").get()
                        .findChild("ccccc").orElse(null));
        Assert.assertNotNull("Failed to add 5th path.",
                root.findChild("aaaa").get()
                        .findChild("bbbb").get()
                        .findChild("ccc").orElse(null));
        Assert.assertNotNull("Failed to add 6th path.",
                root.findChild("aaaa").get()
                        .findChild("bbbb").get()
                        .findChild("cccc").orElse(null));
    }

    @Test
    public void testWalkPath() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        String[] paths = {
                "aaaa/bbbb/ccc",
                "aaa/bbbb/cccc",
                "aaa/bbbb/ccccc",
                "aaa/bbbbb/cccc",
                "aaa/bbbbb/ccccc",
                "aaaa/bbbb/cccc",
        };

        for (String path : paths) {
            root.addPath(path.split("/"));
        }

        Assert.assertNotNull("Failed to get 1st path.",
                root.walkPath(paths[0].split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 2nd path.",
                root.walkPath(paths[1].split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 3rd path.",
                root.walkPath(paths[2].split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 4th path.",
                root.walkPath(paths[3].split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 5th path.",
                root.walkPath(paths[4].split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 6th path.",
                root.walkPath(paths[5].split("/")).orElse(null));

        Assert.assertNotNull("Failed to get 1st parent path.",
                root.walkPath("aaa/bbbb".split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 2nd parent path.",
                root.walkPath("aaaa/bbbb".split("/")).orElse(null));
        Assert.assertNotNull("Failed to get 3rd parent path.",
                root.walkPath("aaa/bbbbb".split("/")).orElse(null));

        Assert.assertNull("Find ghost path.",
                root.walkPath("aaaa/bbbb/c".split("/")).orElse(null));
        Assert.assertNull("Find ghost path.",
                root.walkPath("aaaa/b/cccc".split("/")).orElse(null));
    }
}
