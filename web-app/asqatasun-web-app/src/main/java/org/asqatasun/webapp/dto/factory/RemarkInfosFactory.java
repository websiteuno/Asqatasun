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
package org.asqatasun.webapp.dto.factory;

import org.asqatasun.webapp.dto.RemarkInfosImpl;
import org.asqatasun.webapp.dto.data.RemarkInfos;
import org.springframework.stereotype.Component;

/**
 * 
 * @author jkowalczyk
 */
@Component("remarkInfosFactory")
public final class RemarkInfosFactory {

    /**
     * The holder that handles the unique instance of RemarkInfosFactory
     */
    private static class RemarkInfosFactoryHolder {
        private static final RemarkInfosFactory INSTANCE = new RemarkInfosFactory();
    }

    /**
     * Private constructor
     */
    protected RemarkInfosFactory() {}

    /**
     * Singleton pattern based on the "Initialization-on-demand
     * holder idiom". See @http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
     * @return the unique instance of RemarkInfosFactory
     */
    public static RemarkInfosFactory getInstance() {
        return RemarkInfosFactoryHolder.INSTANCE;
    }

    public RemarkInfos getRemarksInfo(String messageCode){
        return new RemarkInfosImpl(messageCode);
    }

    public RemarkInfos getRemarksInfo(String messageCode, String remarkTarget){
        return new RemarkInfosImpl(messageCode, remarkTarget);
    }

}
