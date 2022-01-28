package org.sindria.xppolicy.controllers;

import org.sindria.xppolicy.models.Policy;
import org.sindria.xppolicy.models.PolicyUser;
import org.sindria.xppolicy.repositories.PolicyRepository;
import org.sindria.xppolicy.repositories.PolicyUserRepository;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@RestController
public class PolicyController extends ApiController {

    private final PolicyRepository policyRepository;

    private final PolicyUserRepository policyUserRepository;

    /**
     * PolicyController constructor
     */
    public PolicyController(PolicyRepository policyRepository, PolicyUserRepository policyUserRepository) {
        this.policyRepository = policyRepository;
        this.policyUserRepository = policyUserRepository;
    }

    @GetMapping("/api/v1/policies")
    public HashMap<String, Object> index(HttpServletResponse response) {

        try {
            Iterable<Policy> policies = this.policyRepository.findAll();

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @GetMapping("/api/v1/policies/{policyId}")
    public HashMap<String, Object> show(@PathVariable Long policyId, HttpServletResponse response) {

        try {
            Optional<Policy> policy = this.policyRepository.findById(policyId);

            if (policy.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policy", policy);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/policies")
    public HashMap<String, Object> store(@Valid @RequestBody Policy newPolicy, HttpServletResponse response) {

        try {
            String name = newPolicy.getName();
            Iterable<Policy> policies = this.policyRepository.findAll();

            // TODO: implement findByName() on policyRepository
            for (var policy : policies) {
                if (policy.getName().equals(name)) {
                    return this.sendError(response, "Policy already exists with this name", 500, new HashMap<String, Object>());
                }
            }

            this.policyRepository.save(newPolicy);

            return this.sendResponse(response,"Policy added successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PutMapping("/api/v1/policies/{policyId}")
    public HashMap<String, Object> edit(@PathVariable long policyId, @Valid @RequestBody Policy editPolicy, HttpServletResponse response) {

        try {
            Policy policy = this.policyRepository.findById(policyId).orElse(null);

            if (policy == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            policy.setName(editPolicy.getName());
            policy.setContent(editPolicy.getContent());
            this.policyRepository.save(policy);

            return this.sendResponse(response,"Policy edited successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @DeleteMapping("/api/v1/policies/{policyId}")
    public HashMap<String, Object> delete(@PathVariable long policyId, HttpServletResponse response) {

        try {
            Policy policy = this.policyRepository.findById(policyId).orElse(null);

            if (policy == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.policyRepository.delete(policy);

            return this.sendResponse(response,"Policy deleted successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/search")
    public HashMap<String, Object> search(@RequestParam String q, HttpServletResponse response) {

        try {
            List<Policy> policies = this.policyRepository.findBySearchTerm(q);

            if (policies.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/user/{userId}")
    public HashMap<String, Object> showUserPolicies(@PathVariable String userId, HttpServletResponse response) {

        try {
            List<PolicyUser> policiesIds = this.policyUserRepository.findByUserId(userId);

            if (policiesIds.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

            ArrayList<Policy> policies = new ArrayList<>();

            for (var entry : policiesIds) {
                Long policyId = entry.getPolicyId();
                Policy policy = this.policyRepository.findById(policyId).orElse(null);

                // Exception
                if (policy == null) {
                    return this.sendError(response, "Error during extract user related policy", 500, new HashMap<String, Object>());
                }

                policies.add(policy);
            }

            if (policies.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/users/{policyId}")
    public HashMap<String, Object> showPolicyUsers(@PathVariable long policyId, HttpServletResponse response) {

        try {
            List<PolicyUser> usersIds = this.policyUserRepository.findByPolicyId(policyId);

            if (usersIds.isEmpty()) {
                return this.sendError(response, "Policy not attached to any user", 404, new HashMap<String, Object>());
            }

            ArrayList<String> users = new ArrayList<>();

            for (var entry : usersIds) {
                String userId = entry.getUserId();
                users.add(userId);
            }

            if (users.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("users", users);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/policies/attach")
    public HashMap<String, Object> attachPolicy(@Valid @RequestBody PolicyUser attachPolicy, HttpServletResponse response) {

        try {
            // TODO: Validate if userId exists by service-to-service REST request

            this.policyUserRepository.save(attachPolicy);
            return this.sendResponse(response,"Policy attached successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/policies/detach")
    public HashMap<String, Object> detachPolicy(@Valid @RequestBody PolicyUser detachPolicy, HttpServletResponse response) {

        try {

            String userId = detachPolicy.getUserId();
            Long policyId = detachPolicy.getPolicyId();

            List<PolicyUser> policiesAttached = this.policyUserRepository.findByUserId(userId);

            if (policiesAttached.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

            Long id = null;

            for (var entry : policiesAttached) {
                Long entryPolicyId = entry.getPolicyId();

                if (entryPolicyId.equals(policyId)) {
                    id = entry.getId();
                }
            }

            // Exception
            if (id == null) {
                return this.sendError(response, "Error during extract entry id", 500, new HashMap<String, Object>());
            }

            PolicyUser policyToDetach = this.policyUserRepository.findById(id).orElse(null);

            if (policyToDetach == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.policyUserRepository.delete(policyToDetach);

            return this.sendResponse(response,"Policy detached successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


}
