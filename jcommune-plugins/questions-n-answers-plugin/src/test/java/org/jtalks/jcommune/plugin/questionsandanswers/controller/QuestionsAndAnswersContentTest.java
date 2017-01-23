package org.jtalks.jcommune.plugin.questionsandanswers.controller;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.EscapeTool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.plugin.api.service.PluginPostService;
import org.jtalks.jcommune.plugin.api.service.UserReader;
import org.jtalks.jcommune.plugin.api.service.nontransactional.BbToHtmlConverter;
import org.jtalks.jcommune.plugin.api.service.nontransactional.PropertiesHolder;
import org.jtalks.jcommune.plugin.api.web.dto.PostDto;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.locale.JcLocaleResolver;
import org.jtalks.jcommune.plugin.api.web.velocity.tool.PermissionTool;
import org.jtalks.jcommune.plugin.questionsandanswers.dto.CommentDto;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.validation.BindingResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 * Created by Mikhail Mangushev on 03.10.2016.
 */
public class QuestionsAndAnswersContentTest {

    private static final String TEMPLATE_PATH = "org/jtalks/jcommune/plugin/questionsandanswers/template/";
    private static final String QUESTION_TEMPLATE_PATH = TEMPLATE_PATH + "question.vm";
    private static final String QUESTION = "question";
    private static final String POST_PAGE = "postPage";
    private static final String CONVERTER = "converter";
    private static final String POST_DTO = "postDto";
    private static final String LIMIT_OF_POSTS_ATTRIBUTE = "postLimit";

    private static final String EMOJI = "\uD83D\uDE02\uD83D\uDE0A";
    private static final String BB_BOLD = "[b]Bold text[/b]";
    private static final String BB_BOLD_CONVERTED = "<span style=\"font-weight:bold;\">Bold text</span>";
    private static final String BB_ITALIC = "[i]Italic text[/i]";
    private static final String BB_ITALIC_CONVERTED = "<span style=\"font-style:italic;\">Italic text</span>";
    private static final String TITLE = "Test title";
    private static final String CONTENT_SOURCE = BB_BOLD + BB_ITALIC + EMOJI;
    private static final String CONTENT_CONVERTED = BB_BOLD_CONVERTED + BB_ITALIC_CONVERTED + EMOJI;

    private static final String HEADER_SELECTOR = "div#branch-header > h1 > a";
    private static final String TITLE_SELECTOR = "title";
    private static final String CONTENT_SELECTOR = "div.content";
    private static final String NEW_POST_SELECTOR =  "textarea#postBody";
    private static final String COMMENT_BODY_SELECTOR = "span.comment-content";
    private static final String COMMENT_SOURCE_SELECTOR = "textarea[name='commentBody']";

    @Mock
    private PluginPostService postService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private BindingResult result;
    @Mock
    private UserReader userReader;
    @Mock
    private PropertiesHolder propertiesHolder;
    @Mock
    private PermissionTool tool;

    @Spy
    private QuestionsAndAnswersController controller = new QuestionsAndAnswersController();

    BbToHtmlConverter converter = (BbToHtmlConverter) BbToHtmlConverter.getInstance();

    @BeforeMethod
    public void init() {
        converter.setBbCodeService(new BBCodeService());

        initMocks(this);
        when(controller.getUserReader()).thenReturn(userReader);
        when(controller.getPluginPostService()).thenReturn(postService);
        when(userReader.getCurrentUser()).thenReturn(new JCUser("name", "example@mail.ru", "pwd"));
        ((JcLocaleResolver)JcLocaleResolver.getInstance()).setUserReader(userReader);
        when(controller.getBbCodeService()).thenReturn(converter);
        when(controller.getProperties()).thenReturn(new Properties());
        when(propertiesHolder.getAllPagesTitlePrefix()).thenReturn(TITLE);
        when(tool.hasPermission(anyLong(), anyString(), anyString())).thenReturn(true);
    }

    @Test
    public void showQuestionTest() {
        Topic topic = createTopic(CONTENT_SOURCE);
        topic.setTitle(EMOJI);

        PostComment comment = getComment(CONTENT_SOURCE);
        topic.getLastPost().addComment(comment);

        PostDto newPost = new PostDto();
        newPost.setTopicId(topic.getId());
        newPost.setBodyText(CONTENT_SOURCE);

        Map<String, Object> model = getDefaultModel();
        model.put(QUESTION, topic);
        model.put(POST_PAGE, new PageImpl<>(topic.getPosts()));
        model.put(CONVERTER, converter);
        model.put(POST_DTO, newPost);
        model.put(LIMIT_OF_POSTS_ATTRIBUTE, QuestionsAndAnswersController.LIMIT_OF_POSTS_VALUE);

        VelocityEngine velocityEngine = new VelocityEngine(getProperties());
        velocityEngine.init();
        String page = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                QUESTION_TEMPLATE_PATH, "UTF-8", model);

        Document doc = Jsoup.parse(page);
        doc.outputSettings().prettyPrint(false);
        String title = doc.select(TITLE_SELECTOR).first().text();
        String header = doc.select(HEADER_SELECTOR).first().text();
        String content = doc.select(CONTENT_SELECTOR).first().html().trim();
        String newPostText = doc.select(NEW_POST_SELECTOR).first().text();
        String commentBody = doc.select(COMMENT_BODY_SELECTOR).first().html();
        String commentSource = doc.select(COMMENT_SOURCE_SELECTOR).first().text();

        assertEquals(title, TITLE + "\n    " + EMOJI);
        assertEquals(header, EMOJI);
        assertEquals(content, CONTENT_CONVERTED);
        assertEquals(newPostText, CONTENT_SOURCE);
        assertEquals(commentBody, CONTENT_CONVERTED);
        assertEquals(commentSource, CONTENT_SOURCE);
    }

    @Test
    public void addCommentTest() throws Exception {
        PostComment comment = getComment(CONTENT_SOURCE);
        CommentDto dto = new CommentDto();
        dto.setPostId(1);
        dto.setBody(comment.getBody());

        when(postService.get(anyLong())).thenReturn(new Post(null, null));
        when(postService.addComment(eq(dto.getPostId()), anyMap(), eq(dto.getBody()))).thenReturn(comment);

        JsonResponse response = controller.addComment(dto, result, request);

        assertEquals(((CommentDto) response.getResult()).getBody(), CONTENT_CONVERTED);
    }

    private PostComment getComment(String commentBody) {
        PostComment comment = new PostComment();
        comment.setAuthor(new JCUser("test", "example@test.com", "pwd"));
        comment.setBody(commentBody);
        comment.setId(1);
        return comment;
    }

    private Topic createTopic(String postContent) {
        Branch branch = new Branch("name", "description");
        branch.setId(1);
        Topic topic = new Topic();
        topic.setId(42L);
        topic.setBranch(branch);
        topic.addPost(new Post(new JCUser("test", "example@test.com", "pwd"), postContent));
        return topic;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("resource.loader", "class");
        properties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        return properties;
    }

    private Map<String, Object> getDefaultModel() {
        Map<String, Object> model = new HashMap<>();
        model.put("esc", new EscapeTool());
        model.put("propertiesHolder", propertiesHolder);
        model.put("permissionTool", tool);
        return model;
    }
}
