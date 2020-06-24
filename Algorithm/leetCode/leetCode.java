class Solution {
    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        int len = nums.length;
        for(int i = 0; i< len;i++)
        {
            for(int j = i + 1; j< len;j++)
            {
                if (nums[i]+nums[j] == target)
                {
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        return result;
    }

    public boolean isPalindrome(int x) {
        if(x < 0)
        {
            return false;
        }
        List<Integer> result = new ArrayList<Integer> ();
        while (x != 0) {

            result.add(x %10);
            x = x /10;
        }
        int len = result.size();
        for (int i = 0;i< len/2;i++)
        {
            if (result.get(i) !=result.get(len-i-1)) {
                return false;
            }
        }
        return true;
    }
}