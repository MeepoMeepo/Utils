package com.shanghai.util.tree;

/**
 * Created by jinlv on 2019/3/6.
 */

public class BinaryTree {
    //	private TreeNode root = new TreeNode();
    public TreeNode getBinaryTree(int[] arr, int index) {
        // TODO Auto-generated method stub
        TreeNode node = null;
        if(index < arr.length){
            int value = arr[index];
            node = new TreeNode(value);
            node.left = getBinaryTree(arr, index*2+1);
            node.right = getBinaryTree(arr, index*2+2);
            return node;
        }
        return node;
    }
}

