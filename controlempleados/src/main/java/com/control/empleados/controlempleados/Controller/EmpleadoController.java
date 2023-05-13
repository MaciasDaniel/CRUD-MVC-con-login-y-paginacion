package com.control.empleados.controlempleados.Controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.empleados.controlempleados.Entities.Empleado;
import com.control.empleados.controlempleados.Pagination.PageRender;
import com.control.empleados.controlempleados.Reportes.EmpleadoExporterExcel;
import com.control.empleados.controlempleados.Reportes.EmpleadoExporterPDF;
import com.control.empleados.controlempleados.Service.EmpleadoService;
import com.lowagie.text.DocumentException;

@Controller
public class EmpleadoController {
    
    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/view/{id}")
    public String verDetallesDelEmpleado(@PathVariable(value = "id") Long id, Map<String, Object> modelo, RedirectAttributes flash) {
        Empleado empleado = empleadoService.findOne(id);
        if(empleado == null) {
            flash.addAttribute("error", "El empleado no existe en la base de datos");
            return "redirect:/listar";
        }
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Detalles del empleado "+empleado.getNombre());
        return "view";
    }

    @GetMapping({"/","/listar",""})
    public String listarEmpleados(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Empleado> empleados = empleadoService.findAll(pageRequest);
        PageRender<Empleado> pageRender = new PageRender<>("listar", empleados);

        modelo.addAttribute("titulo", "Listado de empleados");
        modelo.addAttribute("empleados", empleados);
        modelo.addAttribute("page", pageRender);
        
        return "listar";
    }

    @GetMapping("/form")
    public String mostrarFormularioDeRegistro(Map<String, Object> modelo) {
        Empleado empleado = new Empleado();
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Registro de empleados");
        return "form";
    }

    @PostMapping("/form")
    public String guardarEmpleado(@Valid Empleado empleado, BindingResult result, Model modelo, RedirectAttributes flash, SessionStatus status) {
        if(result.hasErrors()) {
            modelo.addAttribute("titulo", "Registro de empleado");
            return "form";
        }
        String mensaje = (empleado.getId() != null) ? "El empleado ha sido editado con exito" : "Empleado registrado con exito";
        empleadoService.save(empleado);
        status.setComplete();
        flash.addFlashAttribute("success", mensaje);
        return "redirect:/listar";
    }

    @GetMapping("/form/{id}")
    public String editarEmpleado(@PathVariable(value = "id") Long id, Map<String, Object> modelo, RedirectAttributes flash) {
        Empleado empleado = null;
        if(id > 0) {
            empleado = empleadoService.findOne(id);
            if(empleado == null) {
                flash.addFlashAttribute("error", "El empleado no existe en la base de datos");
                return "redirect:/listar";
            }
        }else{
            flash.addAttribute("error", "El ID del empleado no puede ser cero");
            return "redirect:/listar";
        }
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Edición de empleados");
        return "form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if(id > 0) {
            empleadoService.delete(id);
            flash.addFlashAttribute("success", "Empleado eliminado con exito");
        }
        return "redirect:/listar";
    }

    @GetMapping("/exportarPDF")
    public void exportarListadoDeEmpleadosPDF(HttpServletResponse response) throws DocumentException, IOException{
        response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Empleados_" + fechaActual + ".pdf";

        response.setHeader(cabecera, valor);

        List<Empleado> empleados = empleadoService.findAll();

        EmpleadoExporterPDF exporter = new EmpleadoExporterPDF(empleados);
        exporter.exportar(response);
    }

    @GetMapping("/exportarExcel")
    public void exportarListadoDeEmpleadosExcel(HttpServletResponse response) throws DocumentException, IOException{
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Empleados_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Empleado> empleados = empleadoService.findAll();

        EmpleadoExporterExcel exporter = new EmpleadoExporterExcel(empleados);
        exporter.exportar(response);
    }
}
