package org.example;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.problem.AbstractProblem;

import java.util.Arrays;

public class LaborMarketProblem extends AbstractProblem {

    private final int numEmployee;
    private final int numCompany;
    private final double[][] salary;
    private final double[][] distance;  // Khoảng cách từ nhân viên đến công ty
    private final double[][] skill;     // Kỹ năng giữa nhân viên và công ty

    private final int maxEmployeeRequired = 2;  
    private final int maxApplyforEmployee = 1;

    public LaborMarketProblem(int numEmployee, int numCompany, double[][] salary, double[][] distance, double[][] skill) {
        super(1, 1, 2);
        this.numEmployee = numEmployee;
        this.numCompany = numCompany;
        this.salary = salary;
        this.distance = distance;
        this.skill = skill;
    }

    @Override
    public void evaluate(Solution solution) {
        BinaryVariable employeeChosen = (BinaryVariable) solution.getVariable(0);
        double totalSatisfaction = 0;

        int[] validConstraintCompany = new int[numCompany];
        int[] validConstraintEmployee = new int[numEmployee];
        int[] employeeChoice = new int[numEmployee];

        Arrays.fill(validConstraintCompany, 0);
        Arrays.fill(validConstraintEmployee, 0);
        Arrays.fill(employeeChoice, -1);

        // Mảng lưu độ hài lòng của từng cặp nhân viên - công ty
        double[][] employeeCompanySatisfaction = new double[numEmployee][numCompany];

        // Ngưỡng lương tối thiểu để nhân viên cảm thấy hài lòng
        double maxDistance = 100;  // Giả định khoảng cách tối đa để nhân viên cảm thấy hài lòng
        double skillThreshold = 0.5; // Ngưỡng kỹ năng tối thiểu để nhân viên cảm thấy hài lòng

        // Tìm mức lương cao nhất trong bảng lương
        double maxSalary = 0;
        for (int i = 0; i < numEmployee; i++) {
            for (int j = 0; j < numCompany; j++) {
                if (salary[i][j] > maxSalary) {
                    maxSalary = salary[i][j];
                }
            }
        }

        // Tính độ hài lòng và tiền lương
        for (int i = 0; i < numEmployee; i++) {
            for (int j = 0; j < numCompany; j++) {
                int position = i * numCompany + j;
                if (employeeChosen.get(position) && validConstraintEmployee[i] < maxApplyforEmployee && validConstraintCompany[j] < maxEmployeeRequired) {
                    validConstraintCompany[j]++;
                    validConstraintEmployee[i]++;
                    employeeChoice[i] = j;

                    // Tính toán độ hài lòng dựa trên lương, khoảng cách và kỹ năng
                    // Thay đổi cách tính salarySatisfaction để dựa trên mức lương cao nhất
                    double salarySatisfaction = (salary[i][j] / maxSalary) * 10.0;
                    double distanceSatisfaction = (distance[i][j] <= maxDistance) ? (maxDistance - distance[i][j]) / 10.0 : -distance[i][j] / 10.0;
                    double skillSatisfaction = (skill[i][j] >= skillThreshold) ? skill[i][j] * 10.0 : (skill[i][j] - skillThreshold) * 10.0;

                    // Tổng độ hài lòng của nhân viên dựa trên 3 yếu tố
                    double satisfaction = salarySatisfaction + distanceSatisfaction + skillSatisfaction;
                    employeeCompanySatisfaction[i][j] = satisfaction;

                    // Tổng cộng tất cả điểm hài lòng
                    totalSatisfaction += satisfaction;
                } else {
                    employeeCompanySatisfaction[i][j] = 0.0;  // Không có hài lòng nếu không chọn công ty
                }
            }
        }

        // Constraints
        int notSatisfiedForCompany = 0;
        int notSatisfiedForEmployee = 0;

        for (int i : validConstraintCompany) {
            if (i > maxEmployeeRequired) notSatisfiedForCompany++;
        }
        for (int i : validConstraintEmployee) {
            if (i > maxApplyforEmployee) notSatisfiedForEmployee++;
        }

        // Đặt ràng buộc
        solution.setConstraint(0, -notSatisfiedForCompany);
        solution.setConstraint(1, -notSatisfiedForEmployee);

        // Đặt mục tiêu tối ưu hóa
        // Thay đổi mục tiêu tối ưu hóa để tối đa hóa tổng độ hài lòng
        solution.setObjective(0, -totalSatisfaction); // Chúng ta dùng dấu âm để tối đa hóa giá trị này

        // Lưu lựa chọn công ty của nhân viên
        solution.setAttribute("employeeChoice", employeeChoice);

        // Lưu độ hài lòng của từng cặp nhân viên - công ty
        solution.setAttribute("employeeCompanySatisfaction", employeeCompanySatisfaction);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(this.numberOfVariables, this.numberOfObjectives, this.numberOfConstraints);
        solution.setVariable(0, new BinaryVariable(numEmployee * numCompany));  
        return solution;
    }
}
