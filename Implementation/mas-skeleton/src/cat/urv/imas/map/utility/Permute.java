/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.urv.imas.map.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ihcrul
 */
public class Permute{
    private static List<Integer[]> permute(java.util.List<Integer> arr, int k){
        
        List<Integer[]> result = new ArrayList<>();
        
        for(int i = k; i < arr.size(); i++){
            java.util.Collections.swap(arr, i, k);
            result.addAll(permute(arr, k+1));
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() -1){
            result.add(arr.toArray(new Integer[arr.size()]));
        }
        return result;
    }
    
    public static List<Integer[]> getIndexPermutations(int size) {
        List<Integer> indexList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            indexList.add(i);
        }
        return permute(indexList, 0);
    }
    
    public static void main(String[] args){
        List<Integer[]> permuts = getIndexPermutations(3);
        System.err.println(Arrays.toString(permuts.get(3)));
    }
}