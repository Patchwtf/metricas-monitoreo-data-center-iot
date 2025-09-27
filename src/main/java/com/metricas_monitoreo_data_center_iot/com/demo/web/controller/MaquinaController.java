package com.metricas_monitoreo_data_center_iot.com.demo.web.controller;

import com.metricas_monitoreo_data_center_iot.com.demo.enums.EstatusMaquina;
import com.metricas_monitoreo_data_center_iot.com.demo.persistence.entity.MaquinaEntity;
import com.metricas_monitoreo_data_center_iot.com.demo.service.MaquinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/maquinas")
@CrossOrigin(origins = "*")
public class MaquinaController {

    @Autowired
    private MaquinaService maquinaService;

    @GetMapping
    public List<MaquinaEntity> getAllMaquinas() {
        return maquinaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaEntity> getMaquinaById(@PathVariable Integer id) {
        Optional<MaquinaEntity> maquina = maquinaService.findById(id);
        return maquina.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMaquina(@RequestBody MaquinaEntity maquina) {
        if (maquinaService.existeMaquinaConMac(maquina.getMac())) {
            return ResponseEntity.badRequest()
                    .body("Ya existe una máquina con la MAC: " + maquina.getMac());
        }

        MaquinaEntity nuevaMaquina = maquinaService.save(maquina);
        return ResponseEntity.ok(nuevaMaquina);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaquina(
            @PathVariable Integer id, @RequestBody MaquinaEntity maquinaDetails) {

        Optional<MaquinaEntity> maquinaOptional = maquinaService.findById(id);
        if (maquinaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MaquinaEntity maquina = maquinaOptional.get();
        maquina.setMac(maquinaDetails.getMac());
        maquina.setIp(maquinaDetails.getIp());
        maquina.setEstatus(maquinaDetails.getEstatus());

        if (maquinaDetails.getResponsable() != null) {
            maquina.setResponsable(maquinaDetails.getResponsable());
        }

        MaquinaEntity updatedMaquina = maquinaService.save(maquina);
        return ResponseEntity.ok(updatedMaquina);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable Integer id) {  // ← Integer
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