package com.vetcare;

import static org.junit.jupiter.api.Assertions.*;

import com.vetcare.domain.Appointment;
import com.vetcare.domain.Pet;
import com.vetcare.domain.Species;
import com.vetcare.report.CsvReportExporter;
import com.vetcare.report.ReportExporter;
import com.vetcare.report.ReportExporterFactory;
import com.vetcare.report.ReportFormat;
import com.vetcare.repository.AppointmentRepository;
import com.vetcare.repository.PetRepository;
import com.vetcare.service.AppointmentListener;
import com.vetcare.service.AppointmentReminderChecker;
import com.vetcare.strategy.EmergencyFeeStrategy;
import com.vetcare.strategy.StandardFeeStrategy;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PatternsTest {

    private PetRepository petRepository;
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        petRepository = new PetRepository() {
            @Override
            public Pet save(Pet pet) {
                return pet;
            }

            @Override
            public Optional<Pet> findById(long id) {
                return Optional.of(new Pet(id, 1L, "Firulais", Species.DOG, "Mestizo", LocalDate.of(2022, 1, 1)));
            }

            @Override
            public List<Pet> findByOwnerId(long ownerId) {
                return List.of();
            }

            @Override
            public List<Pet> findByName(String name) {
                return List.of();
            }

            @Override
            public void update(Pet pet) {
            }

            @Override
            public void deleteById(long id) {
            }
        };

        appointmentRepository = new AppointmentRepository() {
            private final Map<Long, Appointment> store = new HashMap<>();
            private long seq = 1L;

            @Override
            public Appointment save(Appointment appointment) {
                store.put(seq++, appointment);
                return appointment;
            }

            @Override
            public Optional<Appointment> findById(long id) {
                return Optional.ofNullable(store.get(id));
            }

            @Override
            public List<Appointment> findAll() {
                return new ArrayList<>(store.values());
            }

            @Override
            public List<Appointment> findByTimeSlot(OffsetDateTime scheduledAt) {
                return List.of();
            }

            @Override
            public void update(Appointment appointment) {
            }
        };
    }

    private Appointment cita(OffsetDateTime fecha) {
        return new Appointment(1, 1, fecha, "Chequeo");
    }

    @Test
    void strategyEstandarDevuelveLaTarifaBase() {
        var strategy = new StandardFeeStrategy(new BigDecimal("25"));
        assertEquals(0, new BigDecimal("25.00").compareTo(strategy.calculate(cita(OffsetDateTime.now()))));
    }

    @Test
    void strategyDeEmergenciaAgregaRecargo() {
        var strategy = new EmergencyFeeStrategy(new BigDecimal("25"), new BigDecimal("50"));
        assertEquals(0, new BigDecimal("37.50").compareTo(strategy.calculate(cita(OffsetDateTime.now()))));
    }

    @Test
    void factoryCreaElExportadorSegunElFormato() {
        assertInstanceOf(CsvReportExporter.class, ReportExporterFactory.create(ReportFormat.CSV));
    }

    @Test
    void exportadorCsvEscribeLasFilas(@TempDir Path dir) throws Exception {
        Path archivo = dir.resolve("reporte.csv");
        ReportExporter exporter = ReportExporterFactory.create(ReportFormat.CSV);
        exporter.export(List.of(new String[]{"Mascota", "Costo"}, new String[]{"Firulais", "25.00"}), archivo);
        assertEquals(List.of("Mascota;Costo", "Firulais;25.00"), Files.readAllLines(archivo));
    }

    @Test
    void observerAvisaSoloDeLasCitasProximas() {
        OffsetDateTime ahora = OffsetDateTime.now();
        List<Appointment> avisadas = new ArrayList<>();
        AppointmentListener listener = avisadas::add;

        AppointmentReminderChecker checker = new AppointmentReminderChecker();
        checker.subscribe(listener);
        checker.check(List.of(cita(ahora.plusHours(1)), cita(ahora.plusDays(5))),
                ahora, Duration.ofHours(24));

        assertEquals(1, avisadas.size());
    }
}