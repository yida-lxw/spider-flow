package org.spiderflow.controller;

import org.spiderflow.common.CURDController;
import org.spiderflow.core.model.Variable;
import org.spiderflow.mapper.VariableMapper;
import org.spiderflow.service.VariableServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/variable")
public class VariableController extends CURDController<VariableServiceImpl, VariableMapper, Variable> {
}
