package com.shanghai.util.tree;

/**
 * Created by jinlv on 2019/3/6.
 */

import java.util.Stack;

public class DFS {
    public void getDFS(TreeNode root){
        if(root == null){
            return;
        }
        Stack<TreeNode> stack = new Stack<>();

        stack.push(root);
        while(!stack.isEmpty()){
            TreeNode temp = stack.peek();
            System.out.print(temp.value+"\t");
            stack.pop();
            if(temp.right != null){
                stack.push(temp.right);
            }
            if(temp.left != null){
                stack.push(temp.left);
            }
        }
    }
}

