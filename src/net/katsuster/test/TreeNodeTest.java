package net.katsuster.test;

import java.util.Iterator;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import net.katsuster.scenario.TreeNode;

public class TreeNodeTest {
    @Test
    public void testAddPathSingle() throws Exception {
        TreeNode<String, Integer> root = new TreeNode<>("root", null);
        String path = "aaa/bbbb/cccc";

        root.addPath(path.split("/"), 100);

        Optional<TreeNode<String, Integer>> node = root.findChild("aaa");
        Assert.assertNotNull("Failed to add 1st node.", root.findChild("aaa").orElse(null));
        Assert.assertNull("Add wrong 1st value.", root.findChild("aaa").orElse(null).getValue());
        Assert.assertNull("Add wrong 1st node.", root.findChild("aaaa").orElse(null));
        Assert.assertNull("Add wrong 1st node.", root.findChild("bbbb").orElse(null));
        Assert.assertNull("Add wrong 1st node.", root.findChild("cccc").orElse(null));

        TreeNode<String, Integer> nodeA = root.findChild("aaa").get();
        Assert.assertNotNull("Failed to add 2nd node.", nodeA.findChild("bbbb").orElse(null));
        Assert.assertNull("Add wrong 2nd value.", nodeA.findChild("bbbb").orElse(null).getValue());
        Assert.assertNull("Add wrong 2nd node.", nodeA.findChild("bbbbb").orElse(null));
        Assert.assertNull("Add wrong 2nd node.", nodeA.findChild("aaa").orElse(null));
        Assert.assertNull("Add wrong 2nd node.", nodeA.findChild("cccc").orElse(null));

        TreeNode<String, Integer> nodeB = nodeA.findChild("bbbb").get();
        Assert.assertNotNull("Failed to add 3rd node.", nodeB.findChild("cccc").orElse(null));
        Assert.assertEquals("Add wrong 3rd value.", Integer.valueOf(100), nodeB.findChild("cccc").orElse(null).getValue());
        Assert.assertNull("Add wrong 3rd node.", nodeB.findChild("ccccc").orElse(null));
        Assert.assertNull("Add wrong 3rd node.", nodeB.findChild("aaa").orElse(null));
        Assert.assertNull("Add wrong 3rd node.", nodeB.findChild("bbbb").orElse(null));
    }

    @Test
    public void testAddPathMulti() throws Exception {
        TreeNode<String, Integer> root = new TreeNode<>("root", null);
        String[] paths = {
                "aaaa/bbbb/ccc",
                "aaa/bbbb/cccc",
                "aaa/bbbb/ccccc",
                "aaa/bbbbb/cccc",
                "aaa/bbbbb/ccccc",
                "aaaa/bbbb/cccc",
        };

        int i = 100;
        for (String path : paths) {
            root.addPath(path.split("/"), i);
            i += 100;
        }

        Assert.assertNotNull("Failed to add 1st path.",
                root.findChild("aaa").get()
                        .findChild("bbbb").get()
                        .findChild("cccc").orElse(null));
        Assert.assertEquals("Add wrong 1st value.", Integer.valueOf(200),
                root.findChild("aaa").get()
                        .findChild("bbbb").get()
                        .findChild("cccc").orElse(null).getValue());

        Assert.assertNotNull("Failed to add 2nd path.",
                root.findChild("aaa").get()
                        .findChild("bbbb").get()
                        .findChild("ccccc").orElse(null));
        Assert.assertEquals("Add wrong 2nd value.", Integer.valueOf(300),
                root.findChild("aaa").get()
                        .findChild("bbbb").get()
                        .findChild("ccccc").orElse(null).getValue());

        Assert.assertNotNull("Failed to add 3rd path.",
                root.findChild("aaa").get()
                        .findChild("bbbbb").get()
                        .findChild("cccc").orElse(null));
        Assert.assertEquals("Add wrong 3rd value.", Integer.valueOf(400),
                root.findChild("aaa").get()
                        .findChild("bbbbb").get()
                        .findChild("cccc").orElse(null).getValue());

        Assert.assertNotNull("Failed to add 4th path.",
                root.findChild("aaa").get()
                        .findChild("bbbbb").get()
                        .findChild("ccccc").orElse(null));
        Assert.assertEquals("Add wrong 1st value.", Integer.valueOf(500),
                root.findChild("aaa").get()
                        .findChild("bbbbb").get()
                        .findChild("ccccc").orElse(null).getValue());

        Assert.assertNotNull("Failed to add 5th path.",
                root.findChild("aaaa").get()
                        .findChild("bbbb").get()
                        .findChild("ccc").orElse(null));
        Assert.assertEquals("Add wrong 5th value.", Integer.valueOf(100),
                root.findChild("aaaa").get()
                        .findChild("bbbb").get()
                        .findChild("ccc").orElse(null).getValue());

        Assert.assertNotNull("Failed to add 6th path.",
                root.findChild("aaaa").get()
                        .findChild("bbbb").get()
                        .findChild("cccc").orElse(null));
        Assert.assertEquals("Add wrong 6th value.", Integer.valueOf(600),
                root.findChild("aaaa").get()
                        .findChild("bbbb").get()
                        .findChild("cccc").orElse(null).getValue());
    }

    @Test
    public void testWalkPath() throws Exception {
        TreeNode<String, Integer> root = new TreeNode<>("root", null);
        String[] paths = {
                "aaaa/bbbb/ccc",
                "aaa/bbbb/cccc",
                "aaa/bbbb/ccccc",
                "aaa/bbbbb/cccc",
                "aaa/bbbbb/ccccc",
                "aaaa/bbbb/cccc",
        };

        int i = 100;
        for (String path : paths) {
            root.addPath(path.split("/"), i);
            i += 100;
        }

        Assert.assertNotNull("Failed to get 1st path.",
                root.walkPath(paths[0].split("/")).orElse(null));
        Assert.assertEquals("Add wrong 1st value.", Integer.valueOf(100),
                root.walkPath(paths[0].split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 2nd path.",
                root.walkPath(paths[1].split("/")).orElse(null));
        Assert.assertEquals("Add wrong 2nd value.", Integer.valueOf(200),
                root.walkPath(paths[1].split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 3rd path.",
                root.walkPath(paths[2].split("/")).orElse(null));
        Assert.assertEquals("Add wrong 3rd value.", Integer.valueOf(300),
                root.walkPath(paths[2].split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 4th path.",
                root.walkPath(paths[3].split("/")).orElse(null));
        Assert.assertEquals("Add wrong 4th value.", Integer.valueOf(400),
                root.walkPath(paths[3].split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 5th path.",
                root.walkPath(paths[4].split("/")).orElse(null));
        Assert.assertEquals("Add wrong 5th value.", Integer.valueOf(500),
                root.walkPath(paths[4].split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 6th path.",
                root.walkPath(paths[5].split("/")).orElse(null));
        Assert.assertEquals("Add wrong 6th value.", Integer.valueOf(600),
                root.walkPath(paths[5].split("/")).orElse(null).getValue());

        Assert.assertNotNull("Failed to get 1st parent path.",
                root.walkPath("aaa/bbbb".split("/")).orElse(null));
        Assert.assertNull("Add wrong 1st parent value.",
                root.walkPath("aaa/bbbb".split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 2nd parent path.",
                root.walkPath("aaaa/bbbb".split("/")).orElse(null));
        Assert.assertNull("Add wrong 2nd parent value.",
                root.walkPath("aaaa/bbbb".split("/")).orElse(null).getValue());
        Assert.assertNotNull("Failed to get 3rd parent path.",
                root.walkPath("aaa/bbbbb".split("/")).orElse(null));
        Assert.assertNull("Add wrong 3rd parent value.",
                root.walkPath("aaa/bbbbb".split("/")).orElse(null).getValue());

        Assert.assertNull("Find ghost path.",
                root.walkPath("aaaa/bbbb/c".split("/")).orElse(null));
        Assert.assertNull("Find ghost path.",
                root.walkPath("aaaa/b/cccc".split("/")).orElse(null));
    }

    @Test
    public void testGetChildren() throws Exception {
        TreeNode<String, Integer> root = new TreeNode<>("root", null);
        String[] paths = {
                "aaaa/b1/ccc",
                "aaaa/b1/cccc",
                "aaaa/b2/cccc",
                "aaaa/b3/ccccc",
                "aaaa/b4/cccc",
                "aaa/bbbbb/ccccc",
                "aaa/bbbb/cccc",
        };
        String[] childrenPath = {
                "b1",
                "b2",
                "b3",
                "b4",
        };

        int i = 100;
        for (String path : paths) {
            root.addPath(path.split("/"), i);
            i += 100;
        }

        TreeNode<String, Integer>[] children = root.walkPath("aaaa".split("/")).get().getChildren();
        Assert.assertEquals("Wrong count of children.", childrenPath.length, children.length);
        for (i = 0; i < children.length; i++) {
            Assert.assertEquals("Wrong value of children " + i + ".", children[i].getKey(), childrenPath[i]);
        }
    }

    @Test
    public void testGetChildIterator() throws Exception {
        TreeNode<String, Integer> root = new TreeNode<>("root", null);
        String[] paths = {
                "aaaa/b1/ccc",
                "aaaa/b1/cccc",
                "aaaa/b2/cccc",
                "aaaa/b3/ccccc",
                "aaaa/b4/cccc",
                "aaa/bbbbb/ccccc",
                "aaa/bbbb/cccc",
        };
        String[] childrenPath = {
                "b1",
                "b2",
                "b3",
                "b4",
        };

        int i = 100;
        for (String path : paths) {
            root.addPath(path.split("/"), i);
            i += 100;
        }

        Iterator<TreeNode<String, Integer>> cit = root.walkPath("aaaa".split("/")).get().getChildIterator();
        for (i = 0; cit.hasNext(); i++) {
            Assert.assertEquals("Wrong value of children " + i + ".", cit.next().getKey(), childrenPath[i]);
        }
    }
}
