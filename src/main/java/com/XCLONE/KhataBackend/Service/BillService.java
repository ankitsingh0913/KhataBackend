package com.XCLONE.KhataBackend.Service;

import com.XCLONE.KhataBackend.DTO.bill.BillCreateRequestDTO;
import com.XCLONE.KhataBackend.DTO.bill.BillResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface BillService {

    BillResponseDTO createBill(BillCreateRequestDTO request, UUID userId);

    List<BillResponseDTO> getAllBills(UUID userId);

    BillResponseDTO getBillById(UUID billId, UUID userId);
}
