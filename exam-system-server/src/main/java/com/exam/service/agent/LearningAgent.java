package com.exam.service.agent;

import com.exam.service.agent.model.AgentContext;
import com.exam.service.agent.model.AgentInput;
import com.exam.service.agent.model.AgentOutput;

/**
 * 学习Agent统一接口
 * 所有Agent（ProfileAgent、PlanAgent、GenAgent）都实现此接口
 */
public interface LearningAgent {

    /**
     * 获取Agent角色名
     * @return 如 "ProfileAgent", "PlanAgent", "GenAgent"
     */
    String getRole();

    /**
     * 动态构建system prompt
     * @param ctx Agent上下文
     * @return system prompt字符串
     */
    String buildSystemPrompt(AgentContext ctx);

    /**
     * 执行Agent任务
     * @param input Agent输入
     * @return Agent输出
     */
    AgentOutput execute(AgentInput input);
}
