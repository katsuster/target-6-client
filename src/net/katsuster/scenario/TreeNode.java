package net.katsuster.scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TreeNode<K, V> {
    private K key;
    private V value;
    private TreeNode<K, V> parent;
    private boolean separator;
    private List<TreeNode<K, V>> children = new ArrayList<>();

    public TreeNode(K k, V v) {
        key = k;
        value = v;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V v) {
        value = v;
    }

    public TreeNode<K, V> getParent() {
        return parent;
    }

    public void setParent(TreeNode<K, V> node) {
        parent = node;
    }

    public boolean getSeparator() {
        return separator;
    }

    public void setSeparator(boolean sep) {
        separator = sep;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public TreeNode<K, V>[] getChildren() {
        return children.toArray(new TreeNode[children.size()]);
    }

    public Iterator<TreeNode<K, V>> getChildIterator() {
        return children.iterator();
    }

    public TreeNode<K, V> getChild(int i) {
        return children.get(i);
    }

    public Optional<TreeNode<K, V>> findChild(K k) {
        return children.stream()
                .filter(c -> c.key.equals(k))
                .findFirst();
    }

    public void addChild(TreeNode<K, V> ch) {
        children.add(ch);
        ch.setParent(this);
    }

    public void removeChild(TreeNode<K, V> ch) {
        children.remove(ch);
        ch.setParent(null);
    }

    public void addItemPath(K[] nodes, V value) {
        addPath(nodes, value, false);
    }

    public void addSeparatorPath(K[] nodes, V value) {
        addPath(nodes, value, true);
    }

    public void addPath(K[] nodes, V value, boolean sep) {
        TreeNode<K, V> cnode = this;

        for (K node : nodes) {
            Optional<TreeNode<K, V>> c = cnode.findChild(node);
            TreeNode<K, V> n = null;
            if (c.isEmpty()) {
                n = new TreeNode<>(node, null);
                cnode.addChild(n);
                n.setSeparator(sep);
            }
            cnode = c.orElse(n);
        }

        cnode.setValue(value);
    }

    public Optional<TreeNode<K, V>> walkPath(K[] nodes) {
        Optional<TreeNode<K, V>> c = Optional.of(this);

        for (K node : nodes) {
            if (c.isEmpty()) {
                return c;
            }
            c = c.get().findChild(node);
        }

        return c;
    }
}
