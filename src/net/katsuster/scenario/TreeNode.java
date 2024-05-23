package net.katsuster.scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TreeNode<T> {
    private T value;
    private List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T v) {
        value = v;
    }

    public TreeNode<T>[] getChildren() {
        return (TreeNode<T>[])children.toArray();
    }

    public Iterator<TreeNode<T>> getChildIterator() {
        return children.iterator();
    }

    public TreeNode<T> getChild(int i) {
        return children.get(i);
    }

    public Optional<TreeNode<T>> findChild(T v) {
        return children.stream()
                .filter(c -> c.value.equals(v))
                .findFirst();
    }

    public void addChild(TreeNode<T> ch) {
        children.add(ch);
    }

    public void removeChild(TreeNode<T> ch) {
        children.remove(ch);
    }

    public void addPath(T[] nodes) {
        TreeNode<T> cnode = this;

        for (T node : nodes) {
            Optional<TreeNode<T>> c = cnode.findChild(node);
            TreeNode<T> n = null;
            if (c.isEmpty()) {
                n = new TreeNode<>(node);
                cnode.addChild(n);
            }
            cnode = c.orElse(n);
        }
    }

    public Optional<TreeNode<T>> walkPath(T[] nodes) {
        Optional<TreeNode<T>> c = Optional.of(this);

        for (T node : nodes) {
            if (c.isEmpty()) {
                return c;
            }
            c = c.get().findChild(node);
        }

        return c;
    }
}
