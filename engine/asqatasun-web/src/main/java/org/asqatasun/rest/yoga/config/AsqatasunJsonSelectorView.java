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
package org.asqatasun.rest.yoga.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skyscreamer.yoga.mapper.YogaRequestContext;
import org.skyscreamer.yoga.model.HierarchicalModel;
import org.skyscreamer.yoga.model.ObjectListHierarchicalModelImpl;
import org.skyscreamer.yoga.model.ObjectMapHierarchicalModelImpl;
import org.skyscreamer.yoga.view.JsonSelectorView;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Specific Json selector view which use a {@link ObjectMapper}
 *
 * @author koj
 */
public class AsqatasunJsonSelectorView extends JsonSelectorView {

    private ObjectMapper objectMapper;

    public AsqatasunJsonSelectorView() {
        this.objectMapper = new ObjectMapper();
    }

    public void registerModule(Module module) {
    	objectMapper.registerModule(module);
    }

    @Override
    public void render(Object value, YogaRequestContext requestContext, OutputStream outputStream) throws IOException {
        HierarchicalModel<?> model = null;
        if (value instanceof Iterable<?>) {
            ObjectListHierarchicalModelImpl listModel = new ObjectListHierarchicalModelImpl();
            _resultTraverser.traverseIterable((Iterable<?>) value, requestContext.getSelector(), listModel,
                    requestContext);
            model = listModel;
        } else {
            ObjectMapHierarchicalModelImpl mapModel = new ObjectMapHierarchicalModelImpl();
            _resultTraverser.traversePojo(value, requestContext.getSelector(), mapModel, requestContext);
            model = mapModel;
        }
        objectMapper.writeValue(outputStream, model.getUnderlyingModel());
    }

}
