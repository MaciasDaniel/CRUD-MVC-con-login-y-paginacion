package com.control.empleados.controlempleados.Repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.control.empleados.controlempleados.Entities.Empleado;

public interface EmpleadoRepository extends PagingAndSortingRepository<Empleado, Long>{
    
}
