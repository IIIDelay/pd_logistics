package org.iiidev.pinda.authority.api.fusing;

import org.iiidev.pinda.authority.api.LogApi;
import org.iiidev.pinda.base.Result;

import org.iiidev.pinda.log.entity.OptLogDTO;
import org.springframework.stereotype.Component;

@Component
public class LogApiHystrix implements LogApi {
    public LogApiHystrix() {
    }

    public Result<OptLogDTO> save(OptLogDTO log) {
        return Result.timeout();
    }
}
