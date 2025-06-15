package ebanking.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import ebanking.dto.TransactionDTO;

@Mapper
public interface TransactionMapper {
    List<TransactionDTO> findByCustomerAndMonth(Map<String, Object> params);
    int countByCustomerAndMonth(Map<String, Object> params);
}
