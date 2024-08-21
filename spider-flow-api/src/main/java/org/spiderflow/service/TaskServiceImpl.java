package org.spiderflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.spiderflow.core.model.Task;
import org.spiderflow.core.service.TaskService;
import org.spiderflow.mapper.TaskMapper;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

}
