package org.asqatasun.service;

import java.util.*;

import org.asqatasun.contentadapter.*;
import org.asqatasun.contentadapter.util.URLIdentifierFactory;
import org.asqatasun.contentloader.DownloaderFactory;
import org.asqatasun.entity.audit.Content;
import org.asqatasun.entity.service.audit.ContentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @author jkowalczyk
 */
@Service("contentAdapterService")
public class ContentAdapterServiceImpl implements ContentAdapterService {

//    @Value("${writeCleanHtmlInFile") -> TO DO : use @Value with boolean
    private Boolean writeCleanHtmlInFile = false;
    @Value("${tempFolderRootPath:/var/tmp/asqatasun}")
    private String tempFolderRootPath;
    @Autowired
    private ContentsAdapterFactory contentsAdapterFactory;
    @Autowired
    private HTMLCleanerFactory htmlCleanerFactory;
    @Autowired
    private HTMLParserFactory htmlParserFactory;
    @Autowired
    private URLIdentifierFactory urlIdentifierFactory;
    @Autowired
    private DownloaderFactory downloaderFactory;

    private Set<ContentAdapterFactory> contentAdapterFactorySet;
    @Autowired
    public void setContentAdapterFactorySet(Set<ContentAdapterFactory> contentAdapterFactorySet) {
        this.contentAdapterFactorySet = contentAdapterFactorySet;
    }

    @Autowired
    private ContentDataService contentDataService;

    public ContentAdapterServiceImpl() {
        super();
    }

    @Override
    public Collection<Content> adaptContent(Collection<Content> contentList) {

        Set<ContentAdapter> contentAdapterSet = new HashSet<>();
        
        for (ContentAdapterFactory contentAdapterFactory : contentAdapterFactorySet) {
            contentAdapterSet.add(contentAdapterFactory.create(
                    urlIdentifierFactory.create(), 
                    downloaderFactory.create(), 
                    contentDataService));
        }

        List<Content> localList = new ArrayList<>();
        localList.addAll(contentList);

        ContentsAdapter adapter = contentsAdapterFactory.create(
                localList,
                writeCleanHtmlInFile, 
                tempFolderRootPath, 
                htmlCleanerFactory.create(), 
                htmlParserFactory.create(contentAdapterSet));
        adapter.run();
        return adapter.getResult();
    }

}