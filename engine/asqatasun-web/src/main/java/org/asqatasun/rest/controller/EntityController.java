/*
 * Asqatasun - Automated webpage assessment
 * Copyright (C) 2008-2015  Asqatasun.org
 *
 * This file is part of Asqatasun.
 *
 * Asqatasun is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: asqatasun AT asqatasun DOT org
 */
package org.asqatasun.rest.controller;

import org.asqatasun.rest.exception.AsqatasunException;
import org.asqatasun.sdk.entity.Entity;
import org.asqatasun.sdk.entity.service.GenericDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

/**
 * Created by meskoj on 17/05/16.
 */
public abstract class EntityController<T extends Entity> {

    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    @Autowired
    protected GenericDataService<T, Long> srv;

//    @Autowired(required = false)
//    protected EntityValidator<T> validator;

    @ModelAttribute
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<T> getAll(@RequestParam(value = "view", required = false) String view) {
        return srv.findAll();
    }

    @ModelAttribute
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public T get(@PathVariable Long id, @RequestParam(value = "view", required = false) String view) {
        return srv.read(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long add(@Valid @RequestBody T entity, BindingResult result) throws AsqatasunException {
        if(result.hasErrors()) {
            throw new AsqatasunException(result.getFieldErrors().get(0));
        }
        return srv.saveOrUpdate(entity).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long update(@Valid @RequestBody T entity , BindingResult result) throws AsqatasunException {
        if(result.hasErrors()) {
            throw new AsqatasunException(result.getFieldErrors().get(0));
        }
        return srv.saveOrUpdate(entity).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        srv.delete(id);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
//        binder.addValidators(validator);
    }
}
