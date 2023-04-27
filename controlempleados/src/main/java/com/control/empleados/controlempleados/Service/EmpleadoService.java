package com.control.empleados.controlempleados.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.control.empleados.controlempleados.Entities.Empleado;

public interface EmpleadoService {
    
    public List<Empleado> findAll();

    public Page<Empleado> findAll(Pageable pageable);

    public void save(Empleado empleado);

    public Empleado findOne(Long id);

    public void delete(Long id);
}
