package Meraki.Hub.Management.System.security;

import Meraki.Hub.Management.System.roles.model.Role;
import Meraki.Hub.Management.System.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        log.info(" Checking if ADMIN role exists...");

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setPermissions(null); // or empty set if field is non-null

            roleRepository.save(admin);

            log.info(" ADMIN role created successfully.");
        } else {
            log.info("✔ ADMIN role already exists.");
        }

        // Check if the HR_MANAGER role exists
        log.info("Checking if HR_MANAGER role exists...");
        if (roleRepository.findByName("HR_MANAGER").isEmpty()) {
            Role hrManager = new Role();
            hrManager.setName("HR_MANAGER");
            hrManager.setPermissions(null); // Set permissions if needed
            roleRepository.save(hrManager);
            log.info("HR_MANAGER role created successfully.");
        } else {
            log.info("✔ HR_MANAGER role already exists.");
        }

        // Check if the DEPARTMENT_HEAD role exists
        log.info("Checking if DEPARTMENT_HEAD role exists...");
        if (roleRepository.findByName("DEPARTMENT_HEAD").isEmpty()) {
            Role departmentHead = new Role();
            departmentHead.setName("DEPARTMENT_HEAD");
            departmentHead.setPermissions(null); // Set permissions if needed
            roleRepository.save(departmentHead);
            log.info("DEPARTMENT_HEAD role created successfully.");
        } else {
            log.info("✔ DEPARTMENT_HEAD role already exists.");
        }

        // Check if the EMPLOYEE role exists
        log.info("Checking if EMPLOYEE role exists...");
        if (roleRepository.findByName("EMPLOYEE").isEmpty()) {
            Role employee = new Role();
            employee.setName("EMPLOYEE");
            employee.setPermissions(null); // Set permissions if needed
            roleRepository.save(employee);
            log.info("EMPLOYEE role created successfully.");
        } else {
            log.info("✔ EMPLOYEE role already exist");


        }


    }}
