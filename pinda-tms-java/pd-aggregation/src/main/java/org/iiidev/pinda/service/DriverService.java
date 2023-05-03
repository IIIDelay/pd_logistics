package org.iiidev.pinda.service;

import org.iiidev.pinda.DTO.AppDriverQueryDTO;
import org.iiidev.pinda.DTO.DriverJobDTO;
import org.iiidev.pinda.common.utils.PageResponse;

public interface DriverService {
    PageResponse<DriverJobDTO> findByPage(AppDriverQueryDTO dto);
}
