package parcer;

import java.util.Arrays;

public class Function {

    private final String[] args;
    private final int argsSize;
    private final String fun;

    public Function(String fun, String... args) {
        this.fun = fun;
        this.args = args;
        argsSize = this.args.length;
    }

    public int getArgsSize() {
        return argsSize;
    }

    public String[] getArgs() {
        return args;
    }

    public String getFun() {
        return fun;
    }

    @Override
    public String toString() {
        return "(" + Arrays.toString(args).substring(1).replaceAll("]", "") + ")=" + fun;

    }
}
