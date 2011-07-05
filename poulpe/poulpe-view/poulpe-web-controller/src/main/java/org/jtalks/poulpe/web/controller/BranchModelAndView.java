/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jtalks.poulpe.web.controller;

import java.util.List;
import org.jtalks.poulpe.model.entity.Branch;
import org.jtalks.poulpe.service.BranchService;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Temdegon
 */
@Controller
public class BranchModelAndView {

    private BranchService branchService;
    private Branch selectedBranch;

    public BranchModelAndView() {
        selectedBranch = new Branch();
    }

    public Branch getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(Branch selectedBranch) {
        this.selectedBranch = (selectedBranch != null ? selectedBranch : new Branch());
    }

    public BranchService getBranchService() {
        return branchService;
    }

    public void setBranchService(BranchService branchService) {
        this.branchService = branchService;
    }

    public List<Branch> getBranches() {
        return this.branchService.getAll();
    }

    public void delete() {
        branchService.deleteBranch(selectedBranch);
    }

    public void update() {
        branchService.saveBranch(selectedBranch);
    }

    public void add() {
        Branch newBranch = new Branch();
        newBranch.setName(selectedBranch.getName());
        newBranch.setDescription(selectedBranch.getDescription());
        branchService.saveBranch(newBranch);
    }
}
