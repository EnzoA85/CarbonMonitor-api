package com.capgemini.capcarbon.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.capgemini.capcarbon.dto.request.MaterialRequest;
import com.capgemini.capcarbon.dto.response.MaterialResponse;
import com.capgemini.capcarbon.entity.Material;
import com.capgemini.capcarbon.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public List<MaterialResponse> getAllMaterials() {
        return materialRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MaterialResponse getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));
        return mapToResponse(material);
    }

    public MaterialResponse createMaterial(MaterialRequest request) {
        Material material = Material.builder()
                .name(request.getName())
                .emissionFactor(request.getEmissionFactor())
                .unit(request.getUnit())
                .build();
        Material saved = materialRepository.save(material);
        return mapToResponse(saved);
    }

    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));
        
        material.setName(request.getName());
        material.setEmissionFactor(request.getEmissionFactor());
        material.setUnit(request.getUnit());
        
        Material updated = materialRepository.save(material);
        return mapToResponse(updated);
    }

    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new RuntimeException("Material not found with id: " + id);
        }
        materialRepository.deleteById(id);
    }

    private MaterialResponse mapToResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .name(material.getName())
                .emissionFactor(material.getEmissionFactor())
                .unit(material.getUnit())
                .build();
    }
}
