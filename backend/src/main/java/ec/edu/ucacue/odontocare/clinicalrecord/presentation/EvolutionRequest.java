package ec.edu.ucacue.odontocare.clinicalrecord.presentation;

import jakarta.validation.constraints.NotBlank;

public record EvolutionRequest(@NotBlank String description, Long dentalPieceId) {}

