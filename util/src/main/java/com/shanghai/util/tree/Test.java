package com.shanghai.util.tree;

/**
 * Created by jinlv on 2019/3/6.
 */
public class Test {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int[] arr = new int[]{
                1,2,3,4,5,6,7
        };
        BinaryTree binaryTree = new BinaryTree();
        TreeNode root = binaryTree.getBinaryTree(arr, 0);
        new DFS().getDFS(root);
        System.out.println("\n深度优先遍历结束。。。");
        new BFS().getBFS(root);
        System.out.print("\n广度优先遍历结束。。。");
    }

}
