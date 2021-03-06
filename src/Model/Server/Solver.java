package Model.Server;

public interface Solver<Problem, Solution> {
	public Solution Solve(Problem problem);
}