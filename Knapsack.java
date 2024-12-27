import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Knapsack {

    public static void main(String[] args) {
        Random rand = new Random();
        int numExperiments = 10;
        int numTrials = 10;

        System.out.println("~~~~~~~~~Knapsack Problem (Brute Force & Dynamic Programming)~~~~~~~~~\n\n");

        // Perform a series of experiments
        for (int exp = 1, n = 3; exp <= numExperiments; exp++, n++) {
            int capacity = rand.nextInt(50) + 1; // Random capacity between 1 and 50
            System.out.println("Experiment " + exp + " with n = " + n + " and capacity " + capacity + " :");
            // Run multiple trials for each experiment
            for (int trial = 1; trial <= numTrials; trial++) {
                System.out.println(">>> Trial " + trial + ":");
                int[] weights = new int[n];
                int[] values = new int[n];

                // Generate random weights and values for each item
                for (int i = 0; i < n; i++) {
                    weights[i] = rand.nextInt(15) + 1; // Random weight between 1 and 15
                    values[i] = rand.nextInt(15) + 1; // Random value between 1 and 15
                    System.out.println("Item " + (i + 1) + ": Weight: " + weights[i] + ", Value: " + values[i]);
                }
                System.out.println("");

                // Record the start time of the knapsack algorithm using brute force
                long startTimeBruteForce = System.nanoTime();
                // Solve the knapsack problem using brute force
                Result bruteForceResult = knapsackBruteForce(weights, values, n, capacity);
                // Record the end time of the knapsack algorithm using brute force
                long endTimeBruteForce = System.nanoTime();

                // Record the start time of the knapsack algorithm using dynamic programming
                long startTimeDynamicProgramming = System.nanoTime();
                // Solve the knapsack problem using dynamic programming
                Result dynamicProgrammingResult = knapsackDynamicProgramming(weights, values, n, capacity);
                // Record the end time of the knapsack algorithm using dynamic programming
                long endTimeDynamicProgramming = System.nanoTime();

                // Brute Force Algorithm Solution  
                System.out.println("---------Solution Using Brute Force---------");
                System.out.println("Maximum Value: " + bruteForceResult.totalValue);
                System.out.println("Total Weight: " + bruteForceResult.totalWeight);
                // Find and print the selected items achieving the maximum value using brute force
                List<Integer> selectedItemsBruteForce = bruteForceResult.selectedItems;
                System.out.print("Subset: {");
                for (int i = 0; i < selectedItemsBruteForce.size(); i++) {
                    System.out.print("item " + selectedItemsBruteForce.get(i));
                    if (i < selectedItemsBruteForce.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print("}");
                System.out.println();
                // Print the time taken to solve the problem using brute force
               // System.out.println("Time taken (Brute Force): " + (endTimeBruteForce - startTimeBruteForce) + " nanoseconds");// Print the maximum total value achieved by dynamic programming
               
                System.out.println("-------Solution Using Dynamic Programming-------");
                System.out.println("Maximum Value: " + dynamicProgrammingResult.totalValue);
                System.out.println("Total Weight: " + dynamicProgrammingResult.totalWeight);
                // Find and print the selected items achieving the maximum value using dynamic programming
                List<Integer> selectedItemsDynamicProgramming = dynamicProgrammingResult.selectedItems;
                System.out.print("Subset: {");
                for (int i = 0; i < selectedItemsDynamicProgramming.size(); i++) {
                    System.out.print("item " + selectedItemsDynamicProgramming.get(i));
                    if (i < selectedItemsDynamicProgramming.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print("}");
                System.out.println();
                // Print the time taken to solve the problem using dynamic programming
               // System.out.println("Time taken (Dynamic Programming): " + (endTimeDynamicProgramming - startTimeDynamicProgramming) + " nanoseconds");
               // System.out.println("");
                System.out.println("");

            }
        }
    }

    // Function to solve the knapsack problem using brute force (loops)
    public static Result knapsackBruteForce(int[] weights, int[] values, int n, int capacity) {
        int maxTotalValue = 0;
        int totalWeight = 0;
        List<Integer> selectedItems = new ArrayList<>();

        Stack<State> stack = new Stack<>();
        stack.push(new State(0, 0, 0, new ArrayList<>()));

        // Explore all possible combinations using a stack-based approach
        while (!stack.isEmpty()) {
            State state = stack.pop();

            int index = state.index;
            int currentWeight = state.currentWeight;
            int totalValue = state.totalValue;
            List<Integer> items = new ArrayList<>(state.selectedItems);

            // If all items have been considered or capacity exceeded, update maxTotalValue
            if (index == n || currentWeight > capacity) {
                if (totalValue > maxTotalValue && currentWeight <= capacity) {
                    maxTotalValue = totalValue;
                    totalWeight = currentWeight;
                    selectedItems = items;
                }
                continue;
            }

            // Include current item if adding it doesn't exceed the capacity
            if (currentWeight + weights[index] <= capacity) {
                List<Integer> updatedItems = new ArrayList<>(items);
                updatedItems.add(index + 1);
                stack.push(new State(index + 1, currentWeight + weights[index], totalValue + values[index], updatedItems));
            }

            // Exclude current item
            stack.push(new State(index + 1, currentWeight, totalValue, items));
        }

        return new Result(maxTotalValue, totalWeight, selectedItems);
    }

    // Function to solve the knapsack problem using dynamic programming
    public static Result knapsackDynamicProgramming(int[] weights, int[] values, int n, int capacity) {
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(values[i - 1] + dp[i - 1][w - weights[i - 1]], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }
        int totalValue = dp[n][capacity];
        int totalWeight = 0;
        List<Integer> selectedItems = new ArrayList<>();
        int w = capacity;
        for (int i = n; i > 0 && totalValue > 0; i--) {
            if (totalValue != dp[i - 1][w]) {
                selectedItems.add(i);
                totalValue -= values[i - 1];
                totalWeight += weights[i - 1];
                w -= weights[i - 1];
            }
        }

        // Sort selected items in non-decreasing order
        selectedItems.sort(null);

        return new Result(dp[n][capacity], totalWeight, selectedItems);
    }

    // Function to find the selected items achieving the total value
    static class Result {
        int totalValue;
        int totalWeight;
        List<Integer> selectedItems;

        public Result(int totalValue, int totalWeight, List<Integer> selectedItems) {
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
            this.selectedItems = selectedItems;
        }
    }

    static class State {
        int index;
        int currentWeight;
        int totalValue;
        List<Integer> selectedItems;

        public State(int index, int currentWeight, int totalValue, List<Integer> selectedItems) {
            this.index = index;
            this.currentWeight = currentWeight;
            this.totalValue = totalValue;
            this.selectedItems = selectedItems;
        }
    }
}





