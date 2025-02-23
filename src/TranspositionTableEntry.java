public class TranspositionTableEntry {
    double eval;
    int depth;
    EvalBoard alpha;
    EvalBoard beta;

    public TranspositionTableEntry(double eval, int depth, EvalBoard alpha, EvalBoard beta){
        this.eval = eval;
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
    }
}
