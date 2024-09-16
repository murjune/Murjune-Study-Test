[참고: 안경잡이 개발자](https://blog.naver.com/PostView.naver?blogId=ndb796&logNo=221282478466)

---
# LCA(최소 공통 조상)

LCA(Lowest Common Ancestor)란 트리(Tree)자료 구조에서 두 정점의 공통된 조상 중 가장 가까운 조상을 의미한다.

LCA 알고리즘은 두 정점 사이의 거리를 빠르게 구하는 등 다양하게 활용 가능한 알고리즘이다.

이번 포스팅에서 중점으로 두어야할 LCA알고리즘은 일종의 DP이며, LCA를 빠르게 찾아내는 알고리즘이다.  
LCA 알고리즘은 트리가 BST가 아니고, 일반 트리여도 적용된다는 특징을 갖고 있다.

이에, 앞서 기초 LCA 알고리즘에 대해 먼저 간단하게 알아보자!

# LCA 알고리즘1(feat. DFS)

가장 기본적인 LCA알고리즘은 BOJ LCA-11437문제로 연습할 수 있으며, 풀이는 다음과 같다.

<img width = "600" src = "https://github.com/murjune/today_junelog/raw/main/algorithm/LCA/img1.jpg">
[출처]:(https://www.youtube.com/watch?v=O895NbxirM8&t=3s)

```
1. dfs알고리즘을 통해 모든 노드의 깊이와 부모를 구한다.  

2. 최소 공통 조상을 찾을 두 정점을 확인한다.

    2-1 : 두 정점의 깊이를 맞춘다.(깊이가 더 깊은 노드를 한칸씩 올린다)
    2-2 : 깊이가 같은 두 정점을 부모 방향으로 한 칸씩 올린다.  
    
3. 두 정점이 같아질 때까지 2-2과정을 반복한다.
```

## BOJ LCA-11437번

[문제](https://www.acmicpc.net/problem/11437)  
[풀이: murjune's 깃허브](https://github.com/murjune/today_junelog/blob/main/algorithm/LCA/lca_1.md)

## LCA 1의 시간 복잡도

매 쿼리마다 부모 방향으로 트리를 거슬러 올라가기 위한 최악의 시간 복잡도는 O(N)이다. (unbalanced-Tree)  
따라서, 모든 쿼리를 처리할 때의 총 시간 복잡도는  O(MN)이라 할 수 있겠다.

# LCA 알고리즘 2 (feat. DFS, DP)

## DP를 활용한 LCA 진행 과정

```
1. 모든 노드에 대한 깊이와 1(2^0)번째 부모를 구한다.
2. 모든 노드에 대한 2^i(1<=i<LOG)째 부모 노드를 구한다.  
3. LCA를 찾을 두 노드를 설정한다.
4. 두 노드의 깊이를 맞춘다.(더 깊은 노드를 더 얕은 노드에 맞춤)
5. 루트 노드부터 내려오는 방식으로 LCA를 찾는다.
```

# 0. LCA 알고리즘을 위한 초기 설정


```python
import sys
input = lambda : sys.stdin.readline().rstrip()
sys.setrecursionlimit(10**5)
LOG = 21 # 2^20 = 100만 ,데이터가  100만개까지 들어온다고 가정

# LCA 2(최소 공통 조상 찾기)
n = 15

# 노드의 부모 테이블
parent = [[0]*LOG for _ in range(n+1)]
# 노드의 깊이 테이블
depth = [0] * (n+1)
# dfs 방문 체크 테이블
visited = [False]*(n+1)
# 노드 들의 간선 정보
graph = [[], [2, 3], [1, 4, 6, 5], [1, 7, 8], [2, 9, 10], [2, 11, 12], [2], [3, 13, 14], [3], [4], [4], [5, 15], [5], [7], [7], [11]]

```

## 1. 모든 노드에 대한 깊이와 1(2^0)번째 부모를 구한다.


```python

# 1. dfs함수를 통해 depth + parent[x][0] 테이블 갱신
def dfs(x ,d): # 시작은 root 노드, d = 0

    visited[x] = True

    for i in graph[x]: # x의 자식 노드들
        if not visited[i]:  # 방문한 노드가 아닐 경우
            parent[i][0] = x # i의 한 칸 위의 부모만 기록
            depth[i] = d+1
            dfs(i,d+1) # 깊이 1추가

    return
```

# 2. 모든 노드에 대한 2^i(1<=i<LOG)째 부모 노드를 구한다.


```python
# 2. 모든 부모 노드 세팅하기 - DP

def set_parent():
    dfs(1,0) # 루트 노드 1번
    for i in range(1,LOG):
        for j in range(1,1+n):
            # bottom - up 방식
            # ex) 1은 2의 부모 , 2는 5의 부모
            #  5의 2칸 위 부모 = (5의 1칸 위 부모)의 1칸위 부모
            # 즉, 할아버지 = 아버지의 아버지
            # 증조할아버지 = 할아버지의 아버지
            parent[j][i] = parent[parent[j][i-1]][i-1]
```

# 3. LCA를 찾을 두 노드를 설정한다.


```python
set_parent()
a,b = 6, 11
a2,b2 = 10, 9
a3, b3 = 2, 6
```

# 4. 두 노드의 깊이를 맞춘 후, 루트 노드부터 내려오는 방식으로 LCA를 찾는다.


```python
# 3. 두 정점의 LCA 찾기
def lca(a,b):

    # 1. 두 노드의 깊이 맞추기
    # b를 더 깊이 설정
    if depth[a] > depth[b]:
        a ,b = b,a
    # 더 큰 값 b 점프 -> 깊이 맞추기
    for i in range(LOG-1,-1,-1): # LOG-1~0
        if depth[b] - depth[a] >= (1 << i):
            b = parent[b][i] # b는 b의  2^i번째 부모로 change

    # 2. 부모가 같아 질 때까지, 부모 방향으로 올라가기

    if a == b: return a
    # 2-1. 두 노드를 기준으로 가장 멀리 있는 부모부터 계산해 부모가 동일하지 않는 시점을 찾는다.
    # 2-2. 부모가 동일 하지 않은 만큼 점프를 한다.
    # 2-3. 2-1과 2-2과정 반복
    # 2-4. loop를 마친 후, 정점의 1칸 위가 LCA이다.
    for i in range(LOG-1,-1,-1):
        if parent[a][i] != parent[b][i]:
            a = parent[a][i]
            b = parent[b][i]

    return parent[a][0]

```

# 5. LCA 출력


```python
print(lca(a,b))
print(lca(a2,b2))
print(lca(a3,b3))
```

    2
    4
    2


# BOJ LCA 2 - 11438
[문제](https://www.acmicpc.net/problem/11438)
[풀이: murjune's 깃허브](https://github.com/murjune/today_junelog/blob/main/algorithm/LCA/lca_2.md)
