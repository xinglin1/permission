package com.xmcc.service;

import com.xmcc.beans.PageQuery;
import com.xmcc.beans.SearchLog;
import com.xmcc.model.SysLogWithBLOBs;
import org.springframework.stereotype.Service;

/**
 * @author 张兴林
 * @date 2019-04-02 09:32
 */
@Service
public interface SysLogService {

    void saveLog(String type,String old,String now,Integer target_id);

    PageQuery<SysLogWithBLOBs> search(SearchLog searchLog, PageQuery<SysLogWithBLOBs> pageBean);

    void restore(Integer id);
}
