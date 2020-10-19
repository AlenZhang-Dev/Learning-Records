package Sort;

public class TableInsertionSort {
    public static Node init() {
        int[] a = {49, 38, 65, 97, 76, 13, 27, 49};
        Node head = new Node();
        Node p = head;
        for (int i = 0; i < a.length; i++) {
            Node node = new Node();
            node.setKey(a[i]);
            p.setNode(node);
            p = node;
        }
        return head;
    }

    public static void sort(Node list) {
        Node pre, now, p, q, head;
        head = list;
        pre = list.getNode();
        now = pre.getNode();
        while (now != null) {
            q = head;
            p = q.getNode();
            while (p != now && p.getKey() <= now.getKey()) {
                q = p;
                p = p.getNode();
            }
            if (p == now) {
                pre = pre.getNode();
                now = pre.getNode();
                continue;
            }
            pre.setNode(now.getNode());
            q.setNode(now);
            now.setNode(p);
            now = pre.getNode();
        }
    }

    public static void printList(Node head) {
        Node p = head.getNode();
        while (p != null) {
            System.out.print(p.getKey() + " ");
            p = p.getNode();
        }
    }

    public static void main(String agrs[]) {
        Node list = init();
        sort(list);
        printList(list);
    }

    //set Node class
     static class Node {
        private int key;
        private Node node;

        public Node() {

        }

        public Node(int key, Node node) {
            this.key = key;
            this.node = node;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }
    }
}
