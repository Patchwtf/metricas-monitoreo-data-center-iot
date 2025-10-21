package com.metricas_monitoreo_data_center_iot.com.web.controller;

import com.metricas_monitoreo_data_center_iot.com.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.service.MaquinaService;
import com.metricas_monitoreo_data_center_iot.com.service.dto.MaquinaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maquinas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN') or hasRole('USUARIO')")
public class MaquinaController {

    @Autowired
    private MaquinaService maquinaService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MaquinaDTO>> getAllMaquinas() {
        List<MaquinaEntity> maquinas = maquinaService.findAll();
        List<MaquinaDTO> maquinasDTO = maquinas.stream()
                .map(MaquinaDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(maquinasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaDTO> getMaquinaById(@PathVariable Integer id) {
        Optional<MaquinaEntity> maquina = maquinaService.findById(id);
        return maquina.map(entity -> ResponseEntity.ok(new MaquinaDTO(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MaquinaDTO> createMaquina(@RequestBody MaquinaEntity maquina) {
        try {
            MaquinaEntity nuevaMaquina = maquinaService.save(maquina);
            return ResponseEntity.ok(new MaquinaDTO(nuevaMaquina));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaquina(
            @PathVariable Integer id, @RequestBody MaquinaEntity maquinaDetails) {

        Optional<MaquinaEntity> maquinaOptional = maquinaService.findById(id);
        if (maquinaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MaquinaEntity maquina = maquinaOptional.get();
        maquina.setNombre(maquinaDetails.getNombre());
        maquina.setMac(maquinaDetails.getMac());
        maquina.setIp(maquinaDetails.getIp());
        maquina.setEstatus(maquinaDetails.getEstatus());

        if (maquinaDetails.getResponsable() != null) {
            maquina.setResponsable(maquinaDetails.getResponsable());
        }

        MaquinaEntity updatedMaquina = maquinaService.save(maquina);
        return ResponseEntity.ok(new MaquinaDTO(updatedMaquina));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable Integer id) {
        if (maquinaService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        maquinaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estatus/{estatus}")
    public List<MaquinaEntity> getMaquinasByEstatus(@PathVariable EstatusMaquina estatus) {
        return maquinaService.findByEstatus(estatus);
    }
}