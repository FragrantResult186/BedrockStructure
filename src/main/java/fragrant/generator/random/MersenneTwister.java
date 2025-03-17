package fragrant.generator.random;

public class MersenneTwister {
    private final int[] state = new int[624];
    private int index = 0;

    public static int[] genNums(int s, int n) {
        int[] nums = new int[n];
        int[] st = new int[n + 397];
        st[0] = s;

        for(int i=1; i<st.length; i++)
            st[i] = (int) (0x6C078965L * (st[i - 1] ^ st[i - 1] >>> 30) + i);

        for(int i=0; i<n; i++) {
            int y = (st[i] & 0x80000000) | (st[i+1] & 0x7fffffff);
            nums[i] = st[i+397] ^ (y>>>1) ^ ((y&1) * 0x9908B0DF);
        }

        for(int i=0; i<n; i++)
            nums[i] = temper(nums[i]);

        return nums;
    }

    private static int temper(int y) {
        y ^= y>>>11;
        y ^= (y<<7) & 0x9D2C5680;
        y ^= (y<<15) & 0xEFC60000;
        return y ^ y>>>18;
    }

    public static int mod(int a, int n) {
        return Integer.remainderUnsigned(a, n);
    }

    public void seed(long s, int n) {
        n = n > 0 ? n + 397 : state.length;
        state[0] = (int)s;
        for(int i=1; i<state.length && i<n; i++)
            state[i] = (int) (0x6C078965L * (state[i - 1] ^ state[i - 1] >>> 30) + i);
        index = state.length;
    }

    private void twist() {
        final int M = 397, J = state.length - M;
        for(int i=0; i<state.length; i++) {
            int j = i < J ? i + M : i - J;
            int y = (state[i] & 0x80000000) | (state[(i+1)%state.length] & 0x7fffffff);
            state[i] = state[j] ^ (y>>>1) ^ ((y&1) * 0x9908B0DF);
        }
        index = 0;
    }

    private int next() {
        if(index >= state.length) twist();
        return temper(state[index++]);
    }

    public int nextInt(int n) {
        return (int)((next() & 0xFFFFFFFFL) % n);
    }

    public int nextUnbound() {
        return next() >>> 1;
    }

    public float nextFloat() {
        return (next() & 0xFFFFFFFFL) * (1.0f / 4294967296.0f);
    }

    public static double int2Float(int x) {
        return (x & 0xFFFFFFFFL) * 2.328306436538696e-10;
    }

    public void skip(long n) {
        index = (int)((index + n) % state.length);
    }
}
