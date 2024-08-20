package org.spiderflow.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.spiderflow.core.mapper.DataSourceMapper;
import org.spiderflow.core.model.DataSource;
import org.springframework.stereotype.Service;


@Service
public class DataSourceService extends ServiceImpl<DataSourceMapper, DataSource> {

}
