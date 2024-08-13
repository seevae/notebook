package io.seevae.array.双指针解决数组问题;

public class 移除元素 {
    public static void main(String[] args) {
        //int[] arr = {0, 1, 2, 2, 3, 0, 4, 2};
        int[] arr = {2};
        int result = removeElement(arr, 2);
        System.out.println("需要观察的位数为前: " + result + "位");
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public static int removeElement(int[] nums, int val) {
        int fast = 0, slow = 0;
        while (fast < nums.length) {
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
        return slow;
    }

    /**
     * 测试用例为 nums = {2} , val = 3 的时候不知道为什么leetcode过不去，实际结果和使用方法一其实是相同的
     */
    public static int removeElement2(int[] nums, int val) {
        if (nums.length == 0) {
            return 0;
        }
        int left = 0, right = nums.length - 1, count = 0;

        while (left != right) {
            if (nums[left] != val) {
                left++;
            } else {
                if (nums[right] == val) {
                    right--;
                    int tmp = nums[left];
                    nums[left] = nums[right];
                    nums[right] = tmp;
                } else {
                    int tmp = nums[left];
                    nums[left] = nums[right];
                    nums[right] = tmp;
                }
                left++;
            }
            count++;
        }

        return count;
    }
}
