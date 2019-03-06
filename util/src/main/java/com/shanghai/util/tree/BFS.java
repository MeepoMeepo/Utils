package com.shanghai.util.tree;

/**
 * Created by jinlv on 2019/3/6.
 */
import java.util.ArrayList;


public class BFS {
    public void getBFS(TreeNode root) {
        // TODO Auto-generated method stub
        if(root == null){
            return;
        }
        ArrayList<TreeNode> queue = new ArrayList<>();
        queue.add(root);
        while(queue.size() > 0){
            TreeNode temp = queue.get(0);
            queue.remove(0);
            System.out.print(temp.value+"\t");
            if(temp.left != null){
                queue.add(temp.left);
            }
            if(temp.right != null){
                queue.add(temp.right);
            }
        }
    }
}

