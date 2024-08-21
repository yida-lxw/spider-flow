package org.spiderflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.spiderflow.core.model.DataSource;
import org.spiderflow.core.service.DataSourceService;
import org.spiderflow.mapper.DataSourceMapper;
import org.springframework.stereotype.Service;


@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

}
