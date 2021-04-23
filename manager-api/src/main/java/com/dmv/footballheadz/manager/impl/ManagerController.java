package com.dmv.footballheadz.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.amazonaws.util.CollectionUtils.isNullOrEmpty;
import static org.springframework.http.HttpStatus.*;

@CrossOrigin
@RestController
@RequestMapping("/v1")
public class ManagerController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ManagerService service;

    @RequestMapping(path = "/manager", method = RequestMethod.GET)
    public ResponseEntity<List<Manager>> list() {
        log.trace("Entering list()");
        List<Manager> managers = service.list();
        if (managers.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        return new ResponseEntity<>(managers, OK);
    }

    @RequestMapping(path = "/manager/", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> retrieveManagerTeams(@RequestParam(value="managerName") String managerName) {
        log.trace("Entering retrieveManagerTeams() for {}", managerName);
        List<Manager> managers = service.retrieveTeams(managerName);
        if (managers.isEmpty() || isNullOrEmpty(managers.get(0).getTeams())) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        return Optional.of(managers.get(0).getTeams())
                .map(teams -> new ResponseEntity<>(teams, OK))
                .orElse(new ResponseEntity<>(NO_CONTENT));
    }

    @RequestMapping(path ="/manager/{managerName}", method = RequestMethod.GET)
    public ResponseEntity<Manager> read(@PathVariable String managerName) {
        log.trace("Enter read for {}", managerName);
        return service.read(managerName)
                .map(manager -> new ResponseEntity<>(manager, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/manager", method = RequestMethod.POST)
    public ResponseEntity<Manager> create(@RequestBody @Valid Manager manager) {
        log.trace("Entering create() with {}", manager);
        return service.create(manager)
                .map(newManagerData -> new ResponseEntity<>(newManagerData, CREATED))
                .orElse(new ResponseEntity<>(CONFLICT));
    }

    @RequestMapping(path = "/manager/{managerName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String managerName) {
        log.trace("Entering delete() with {}", managerName);
        return service.delete(managerName) ?
                new ResponseEntity<>(NO_CONTENT) :
                new ResponseEntity<>(NOT_FOUND);
    }

    @RequestMapping(path = "/manager/{managerName}", method = RequestMethod.PUT)
    public ResponseEntity<Manager> put(@PathVariable String managerName, @RequestBody Manager manager) {
        log.trace("Entering put() with {}, {}", managerName, manager);
        return service.replace(manager.withManagerName(managerName))
                .map(newManagerData -> new ResponseEntity<>(newManagerData, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    @RequestMapping(path = "/manager/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Manager> patch(@PathVariable String managerName, @RequestBody Manager manager) {
        log.trace("Entering patch() with {}, {}", managerName, manager);
        return service.update(manager.withManagerName(managerName))
                .map(newManagerData -> new ResponseEntity<>(newManagerData, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }
}
