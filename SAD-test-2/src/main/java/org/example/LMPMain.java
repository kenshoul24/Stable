package org.example;

import java.text.DecimalFormat;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class LMPMain {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void main(String[] args) {
        int numEmployee = 20;
        int numCompany = 5;

        // Bảng lương của nhân viên với từng công ty
        double[][] salary = {
            {4500, 6000, 3000, 4000, 4800},
            {4800, 5500, 6000, 2000, 5200},
            {6000, 3000, 4000, 6500, 2500},
            {5200, 6200, 4000, 3500, 5400},
            {4500, 5000, 5200, 4700, 5800},
            {5100, 5800, 5600, 4900, 3500},
            {4700, 5400, 5100, 4500, 5700},
            {5300, 5600, 5500, 4700, 6000},
            {4900, 5200, 5100, 4600, 5800},
            {4700, 5400, 5000, 4800, 5600},
            {5000, 2500, 5400, 4700, 5900},
            {4600, 5200, 5300, 4400, 5700},
            {4900, 5100, 5200, 4600, 5800},
            {5300, 5400, 5500, 4700, 6100},
            {4600, 5000, 5200, 4300, 5500},
            {5100, 5600, 5500, 4900, 5700},
            {4700, 5200, 5000, 4500, 5900},
            {5300, 5700, 5400, 4800, 6000},
            {4900, 5200, 5100, 4600, 5600},
            {4600, 5100, 5300, 4400, 5500}
        };

        // Bảng khoảng cách của nhân viên đến từng công ty
        double[][] distance = {
            {5, 20, 50, 70, 100},
            {10, 25, 55, 60, 90},
            {15, 30, 40, 50, 85},
            {20, 35, 60, 70, 80},
            {5, 15, 45, 55, 75},
            {25, 40, 50, 60, 95},
            {30, 35, 55, 65, 70},
            {5, 25, 45, 65, 90},
            {10, 20, 40, 50, 85},
            {15, 30, 55, 60, 80},
            {5, 25, 45, 65, 90},
            {10, 35, 55, 70, 85},
            {20, 40, 60, 80, 100},
            {15, 25, 55, 70, 95},
            {10, 20, 40, 65, 90},
            {5, 30, 50, 70, 85},
            {25, 40, 65, 80, 100},
            {30, 50, 75, 85, 95},
            {15, 35, 55, 65, 90},
            {10, 20, 40, 50, 75}
        };

        // Bảng kỹ năng giữa nhân viên và công ty
        double[][] skill = {
            {0.7, 0.6, 0.8, 0.4, 0.5},
            {0.5, 0.7, 0.6, 0.9, 0.8},
            {0.9, 0.5, 0.7, 0.8, 0.6},
            {0.4, 0.8, 0.5, 0.6, 0.7},
            {0.6, 0.7, 0.8, 0.5, 0.9},
            {0.8, 0.5, 0.7, 0.9, 0.4},
            {0.6, 0.7, 0.5, 0.8, 0.9},
            {0.9, 0.8, 0.7, 0.6, 0.5},
            {0.7, 0.9, 0.6, 0.5, 0.8},
            {0.8, 0.5, 0.9, 0.7, 0.6},
            {0.6, 0.8, 0.5, 0.7, 0.9},
            {0.9, 0.6, 0.8, 0.5, 0.7},
            {0.7, 0.9, 0.5, 0.8, 0.6},
            {0.8, 0.7, 0.9, 0.5, 0.6},
            {0.5, 0.9, 0.6, 0.7, 0.8},
            {0.6, 0.5, 0.8, 0.9, 0.7},
            {0.7, 0.8, 0.5, 0.9, 0.6},
            {0.9, 0.7, 0.6, 0.8, 0.5},
            {0.5, 0.9, 0.8, 0.7, 0.6},
            {0.6, 0.7, 0.9, 0.8, 0.5}
        };

        NondominatedPopulation result = new Executor()
            .withProblemClass(LaborMarketProblem.class, numEmployee, numCompany, salary, distance, skill)
            .withAlgorithm("NSGAII")
            .withMaxEvaluations(10000)
            .run();

        // Hiển thị kết quả
        for (Solution solution : result) {
            int[] employeeChoice = (int[]) solution.getAttribute("employeeChoice");
            double[][] employeeCompanySatisfaction = (double[][]) solution.getAttribute("employeeCompanySatisfaction");

            double totalSalaryAcquired = -solution.getObjective(0);

            System.out.println("=================================================");
            System.out.println("Summary of Results:");
            System.out.println("-------------------------------------------------");
            System.out.println("Total Salary Acquired: " + df.format(totalSalaryAcquired) + " USD");
            System.out.println("Constraint Violations:");
            System.out.println("  - Company constraints: " + df.format(-solution.getConstraint(0)));
            System.out.println("  - Employee constraints: " + df.format(-solution.getConstraint(1)));
            System.out.println("-------------------------------------------------");

            System.out.println("Employee Assignments:");
            System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n", "Employee", "Company", "Salary (USD)", "Distance", "Skill", "Satisfaction");
            System.out.println("-------------------------------------------------");

            for (int i = 0; i < employeeChoice.length; i++) {
                int company = employeeChoice[i];
                if (company >= 0) {
                    double employeeSalary = salary[i][company];
                    double employeeDistance = distance[i][company];
                    double employeeSkill = skill[i][company];
                    double satisfaction = employeeCompanySatisfaction[i][company];
                    System.out.printf("%-12d%-12d%-12s%-12s%-12s%-12s\n", (i + 1), (company + 1), df.format(employeeSalary), df.format(employeeDistance), df.format(employeeSkill), df.format(satisfaction));
                } else {
                    System.out.printf("%-12d%-12s%-12s%-12s%-12s%-12s\n", (i + 1), "None", "N/A", "N/A", "N/A", "N/A");
                }
            }
            System.out.println("=================================================");
        }
    }
}
