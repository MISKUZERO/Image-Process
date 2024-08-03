package com.mikkku.util.digest;

/**
 * @author MiskuZero
 * @date 2024/8/4 0:09
 */
public interface LongDigest<T> extends DigestUtil<T, Long> {

    /**
     * 计算两串哈希值的重合度，此方法会对哈希值逐比特位对比<b>出错位数</b>大于<b>退出阈值</b>可以提前结束。<br>
     * 例如，两位哈希值分别为 10110011 和 11001110，一共有 6 位比特位不一样，所以重合度为 2/8 即返回值 0.25，
     * 如果<b>退出阈值</b>为 3，<b>出错位数</b>：6 大于<b>退出阈值</b>：3，会提前结束，并返回 -1。
     *
     * @param hash1         哈希值1
     * @param hash2         哈希值2
     * @param len           哈希值的二进制位数
     * @param quitThreshold 退出阈值
     * @return 重合度或 -1（<b>出错位数</b>大于<b>退出阈值</b>）。
     */
    static double match(long hash1, long hash2, int len, int quitThreshold) {
        long res = hash1 ^ hash2;
        int missCnt = 0;
        for (int i = 0; i != len; i++) {
            if (((res >>> i) & 1) == 1)
                missCnt++;
            if (missCnt == quitThreshold) return -1;
        }
        return 1 - (double) missCnt / len;
    }
}
