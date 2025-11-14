class TokenInfo {
    constructor(req) {
        this.req = req;
    }

    // Middleware để attach TokenInfo vào req
    static attach() {
        return (req, res, next) => {
            req.tokenInfo = new TokenInfo(req);
            next();
        };
    }

    getUserId() {
        const id = this.req.headers['x-user-id'];
        return id ? parseInt(id) : null;
    }

    getUsername() {
        return this.req.headers['x-username'] || null;
    }

    getRoles() {
        const rolesHeader = this.req.headers['x-role'];
        if (!rolesHeader) return [];
        return rolesHeader.split(',').map(r => r.trim());
    }

    hasRole(role) {
        return this.getRoles().includes(role);
    }
}

module.exports = TokenInfo;
